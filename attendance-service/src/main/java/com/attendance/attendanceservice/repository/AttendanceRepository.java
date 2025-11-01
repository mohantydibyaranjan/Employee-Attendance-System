package com.attendance.attendanceservice.repository;

import com.attendance.common.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByEmployeeIdAndCheckInTimeBetween(Long employeeId, LocalDateTime startOfDay, LocalDateTime endOfDay);
    List<Attendance> findByEmployeeId(Long employeeId);
}
