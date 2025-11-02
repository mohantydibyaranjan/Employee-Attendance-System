package com.attendance.attendanceservice.controller;

import com.attendance.common.dto.ApiResponse;
import com.attendance.common.dto.AttendanceDto;
import com.attendance.common.entity.Attendance;
import com.attendance.attendanceservice.service.AttendanceService;
import com.attendance.common.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/attendance")
@Tag(name = "Attendance Management", description = "APIs for managing employee attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private JwtUtil jwtUtil;

    private void checkHasEmployeeOrAdminRole(String authHeader) {
        String role = jwtUtil.extractRole(authHeader.substring(7));
        if (!"ADMIN".equals(role) && !"EMPLOYEE".equals(role)) {
            throw new com.attendance.common.exception.AccessDeniedException("You do not have permission to perform this action.");
        }
    }

    @Operation(summary = "Check in", description = "Records an employee's check-in.")
    @PostMapping("/checkin")
    public ResponseEntity<ApiResponse<AttendanceDto>> checkIn(@RequestHeader("Authorization") String authHeader) {
        checkHasEmployeeOrAdminRole(authHeader);
        Long employeeId = jwtUtil.extractEmployeeId(authHeader.substring(7));
        Attendance attendance = attendanceService.checkIn(employeeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Check-in successful", convertToDto(attendance)));
    }

    @Operation(summary = "Check out", description = "Records an employee's check-out.")
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<AttendanceDto>> checkOut(@RequestHeader("Authorization") String authHeader) {
        checkHasEmployeeOrAdminRole(authHeader);
        Long employeeId = jwtUtil.extractEmployeeId(authHeader.substring(7));
        Attendance attendance = attendanceService.checkOut(employeeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Check-out successful", convertToDto(attendance)));
    }

    @Operation(summary = "Get today's attendance", description = "Retrieves today's attendance for a given employee.")
    @GetMapping("/today/{employeeId}")
    public ResponseEntity<ApiResponse<AttendanceDto>> getTodayAttendance(@RequestHeader("Authorization") String authHeader, @PathVariable Long employeeId) {
        checkHasEmployeeOrAdminRole(authHeader);
        String role = jwtUtil.extractRole(authHeader.substring(7));
        Long requesterId = jwtUtil.extractEmployeeId(authHeader.substring(7));
        if (!"ADMIN".equals(role) && !requesterId.equals(employeeId)) {
            throw new com.attendance.common.exception.AccessDeniedException("You can only view your own attendance.");
        }
        Attendance attendance = attendanceService.getTodayAttendance(employeeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Today's attendance retrieved successfully", convertToDto(attendance)));
    }

    @Operation(summary = "Get attendance history", description = "Retrieves the attendance history for a given employee.")
    @GetMapping("/history/{employeeId}")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> getAttendanceHistory(@RequestHeader("Authorization") String authHeader, @PathVariable Long employeeId) {
        checkHasEmployeeOrAdminRole(authHeader);
        String role = jwtUtil.extractRole(authHeader.substring(7));
        Long requesterId = jwtUtil.extractEmployeeId(authHeader.substring(7));
        if (!"ADMIN".equals(role) && !requesterId.equals(employeeId)) {
            throw new com.attendance.common.exception.AccessDeniedException("You can only view your own attendance history.");
        }
        List<Attendance> attendanceHistory = attendanceService.getAttendanceHistory(employeeId);
        List<AttendanceDto> attendanceDtos = attendanceHistory.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "Attendance history retrieved successfully", attendanceDtos));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<Page<AttendanceDto>>> filterAttendance(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Pageable pageable) {
        checkHasEmployeeOrAdminRole(authHeader);
        String role = jwtUtil.extractRole(authHeader.substring(7));
        Long requesterId = jwtUtil.extractEmployeeId(authHeader.substring(7));
        if (!"ADMIN".equals(role)) {
            employeeId = requesterId;
        }
        Specification<Attendance> spec = com.attendance.attendanceservice.util.SpecificationBuilder.build(employeeId,
                startDate != null ? java.time.LocalDate.parse(startDate) : null,
                endDate != null ? java.time.LocalDate.parse(endDate) : null);
        Page<Attendance> attendance = attendanceService.searchAttendance(spec, pageable);
        Page<AttendanceDto> attendanceDtos = attendance.map(this::convertToDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Attendance records retrieved successfully", attendanceDtos));
    }

    private AttendanceDto convertToDto(Attendance attendance) {
        AttendanceDto attendanceDto = new AttendanceDto();
        attendanceDto.setId(attendance.getId());
        attendanceDto.setEmployeeId(attendance.getEmployeeId());
        attendanceDto.setCheckInTime(attendance.getCheckInTime());
        attendanceDto.setCheckOutTime(attendance.getCheckOutTime());
        attendanceDto.setTotalHours(attendance.getTotalHours());
        attendanceDto.setStatus(attendance.getStatus());
        return attendanceDto;
    }
}
