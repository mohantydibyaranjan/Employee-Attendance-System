package com.attendance.reportservice.repository;

import com.attendance.reportservice.entity.AttendanceReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttendanceReportRepository extends JpaRepository<AttendanceReport, Long>, JpaSpecificationExecutor<AttendanceReport> {
    Optional<AttendanceReport> findByEmployeeIdAndMonth(Long employeeId, String month);
}
