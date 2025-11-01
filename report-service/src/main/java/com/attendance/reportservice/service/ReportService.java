package com.attendance.reportservice.service;

import com.attendance.common.entity.Attendance;
import com.attendance.reportservice.entity.AttendanceReport;
import com.attendance.reportservice.repository.AttendanceReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private AttendanceReportRepository reportRepository;

    public AttendanceReport getMonthlySummary(Long employeeId, String month) {
        return reportRepository.findByEmployeeIdAndMonth(employeeId, month)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    public List<AttendanceReport> getAllReports() {
        return reportRepository.findAll();
    }

    public void generateReport(Attendance attendance) {
        String month = attendance.getCheckInTime().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        AttendanceReport report = reportRepository.findByEmployeeIdAndMonth(attendance.getEmployeeId(), month)
                .orElse(new AttendanceReport());

        report.setEmployeeId(attendance.getEmployeeId());
        report.setMonth(month);
        report.setTotalWorkingDays(report.getTotalWorkingDays() == null ? 1 : report.getTotalWorkingDays() + 1);

        if (attendance.getCheckInTime().toLocalTime().isAfter(java.time.LocalTime.of(9, 30))) {
            report.setLateCheckIns(report.getLateCheckIns() == null ? 1 : report.getLateCheckIns() + 1);
        }

        // This is a simplified calculation. A more accurate calculation would involve storing daily working hours and averaging them.
        report.setAverageWorkingHours(
                (report.getAverageWorkingHours() == null ? 0 : report.getAverageWorkingHours() * (report.getTotalWorkingDays() - 1) + attendance.getTotalHours()) / report.getTotalWorkingDays()
        );

        reportRepository.save(report);
    }
}
