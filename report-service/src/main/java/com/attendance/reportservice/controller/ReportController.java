package com.attendance.reportservice.controller;

import com.attendance.common.dto.ApiResponse;
import com.attendance.reportservice.entity.AttendanceReport;
import com.attendance.reportservice.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @Operation(summary = "Get monthly summary", description = "Retrieves the monthly attendance summary for a given employee.")
    @GetMapping("/summary/monthly")
    public ResponseEntity<ApiResponse<AttendanceReport>> getMonthlySummary(@RequestParam Long employeeId, @RequestParam String month) {
        AttendanceReport report = reportService.getMonthlySummary(employeeId, month);
        return ResponseEntity.ok(new ApiResponse<>(true, "Monthly summary retrieved successfully", report));
    }

    @Operation(summary = "Get daily summary", description = "Retrieves the daily attendance summary.")
    @GetMapping("/summary/daily")
    public ResponseEntity<ApiResponse<List<AttendanceReport>>> getDailySummary() {
        // This is a placeholder for a more complex implementation
        List<AttendanceReport> reports = reportService.getAllReports();
        return ResponseEntity.ok(new ApiResponse<>(true, "Daily summary retrieved successfully", reports));
    }

    @Operation(summary = "Search reports", description = "Searches for attendance reports based on the given criteria.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<AttendanceReport>>> searchReports(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String month,
            Pageable pageable) {
        Specification<AttendanceReport> spec = com.attendance.reportservice.util.SpecificationBuilder.build(employeeId, month);
        Page<AttendanceReport> reports = reportService.searchReports(spec, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reports retrieved successfully", reports));
    }
}
