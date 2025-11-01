package com.attendance.reportservice.controller;

import com.attendance.common.dto.ApiResponse;
import com.attendance.reportservice.entity.AttendanceReport;
import com.attendance.reportservice.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/summary/monthly")
    public ResponseEntity<ApiResponse<AttendanceReport>> getMonthlySummary(@RequestParam Long employeeId, @RequestParam String month) {
        AttendanceReport report = reportService.getMonthlySummary(employeeId, month);
        return ResponseEntity.ok(new ApiResponse<>(true, "Monthly summary retrieved successfully", report));
    }

    @GetMapping("/summary/daily")
    public ResponseEntity<ApiResponse<List<AttendanceReport>>> getDailySummary() {
        // This is a placeholder for a more complex implementation
        List<AttendanceReport> reports = reportService.getAllReports();
        return ResponseEntity.ok(new ApiResponse<>(true, "Daily summary retrieved successfully", reports));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<AttendanceReport>>> searchReports(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String month,
            Pageable pageable) {
        Specification<AttendanceReport> spec = (root, query, criteriaBuilder) -> {
            if (employeeId == null && month == null) {
                return criteriaBuilder.conjunction();
            }
            if (employeeId != null) {
                return criteriaBuilder.equal(root.get("employeeId"), employeeId);
            }
            return criteriaBuilder.equal(root.get("month"), month);
        };
        Page<AttendanceReport> reports = reportService.searchReports(spec, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reports retrieved successfully", reports));
    }
}
