package com.attendance.reportservice.controller;

import com.attendance.reportservice.entity.AttendanceReport;
import com.attendance.reportservice.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<AttendanceReport> getMonthlySummary(@RequestParam Long employeeId, @RequestParam String month) {
        return ResponseEntity.ok(reportService.getMonthlySummary(employeeId, month));
    }

    @GetMapping("/summary/daily")
    public ResponseEntity<List<AttendanceReport>> getDailySummary() {
        // This is a placeholder for a more complex implementation
        return ResponseEntity.ok(reportService.getAllReports());
    }
}
