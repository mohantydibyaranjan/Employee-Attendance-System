package com.attendance.reportservice.controller;

import com.attendance.common.dto.ApiResponse;
import com.attendance.reportservice.entity.AttendanceReport;
import com.attendance.common.util.JwtUtil;
import com.attendance.reportservice.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/report")
@Tag(name = "Report Management", description = "APIs for generating and retrieving attendance reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private JwtUtil jwtUtil;

    private void checkAdminRole(String authHeader) {
        if (!"ADMIN".equals(jwtUtil.extractRole(authHeader.substring(7)))) {
            throw new com.attendance.common.exception.AccessDeniedException("You do not have permission to perform this action.");
        }
    }

    @Operation(summary = "Get monthly summary", description = "Retrieves the monthly attendance summary for a given employee.")
    @GetMapping("/summary/monthly")
    public ResponseEntity<ApiResponse<AttendanceReport>> getMonthlySummary(@RequestHeader("Authorization") String authHeader, @RequestParam Long employeeId, @RequestParam String month) {
        checkAdminRole(authHeader);
        AttendanceReport report = reportService.getMonthlySummary(employeeId, month);
        return ResponseEntity.ok(new ApiResponse<>(true, "Monthly summary retrieved successfully", report));
    }

    @Operation(summary = "Get daily summary", description = "Retrieves the daily attendance summary.")
    @GetMapping("/summary/daily")
    public ResponseEntity<ApiResponse<List<AttendanceReport>>> getDailySummary(@RequestHeader("Authorization") String authHeader) {
        checkAdminRole(authHeader);
        // This is a placeholder for a more complex implementation
        List<AttendanceReport> reports = reportService.getAllReports();
        return ResponseEntity.ok(new ApiResponse<>(true, "Daily summary retrieved successfully", reports));
    }

    @Operation(summary = "Search reports", description = "Searches for attendance reports based on the given criteria.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<AttendanceReport>>> searchReports(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String month,
            Pageable pageable) {
        checkAdminRole(authHeader);
        Specification<AttendanceReport> spec = com.attendance.reportservice.util.SpecificationBuilder.build(employeeId, month);
        Page<AttendanceReport> reports = reportService.searchReports(spec, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reports retrieved successfully", reports));
    }
}
