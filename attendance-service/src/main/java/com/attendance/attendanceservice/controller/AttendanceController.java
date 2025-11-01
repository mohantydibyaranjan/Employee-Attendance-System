package com.attendance.attendanceservice.controller;

import com.attendance.common.dto.ApiResponse;
import com.attendance.common.entity.Attendance;
import com.attendance.attendanceservice.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/checkin")
    public ResponseEntity<ApiResponse<Attendance>> checkIn(@RequestHeader("employeeId") Long employeeId) {
        Attendance attendance = attendanceService.checkIn(employeeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Check-in successful", attendance));
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<Attendance>> checkOut(@RequestHeader("employeeId") Long employeeId) {
        Attendance attendance = attendanceService.checkOut(employeeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Check-out successful", attendance));
    }

    @GetMapping("/today/{employeeId}")
    public ResponseEntity<ApiResponse<Attendance>> getTodayAttendance(@PathVariable Long employeeId) {
        Attendance attendance = attendanceService.getTodayAttendance(employeeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Today's attendance retrieved successfully", attendance));
    }

    @GetMapping("/history/{employeeId}")
    public ResponseEntity<ApiResponse<List<Attendance>>> getAttendanceHistory(@PathVariable Long employeeId) {
        List<Attendance> attendanceHistory = attendanceService.getAttendanceHistory(employeeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Attendance history retrieved successfully", attendanceHistory));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<Attendance>>> searchAttendance(
            @RequestParam(required = false) Long employeeId,
            Pageable pageable) {
        Specification<Attendance> spec = (root, query, criteriaBuilder) -> {
            if (employeeId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("employeeId"), employeeId);
        };
        Page<Attendance> attendance = attendanceService.searchAttendance(spec, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Attendance records retrieved successfully", attendance));
    }
}
