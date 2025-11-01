package com.attendance.attendanceservice.service;

import com.attendance.common.entity.Attendance;
import com.attendance.attendanceservice.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String ATTENDANCE_TOPIC = "attendance-topic";
    private static final String ONLINE_EMPLOYEES_KEY = "online_employees";

    public Attendance checkIn(Long employeeId) {
        Attendance attendance = new Attendance();
        attendance.setEmployeeId(employeeId);
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setStatus("PRESENT");
        attendanceRepository.save(attendance);

        redisTemplate.opsForSet().add(ONLINE_EMPLOYEES_KEY, employeeId.toString());
        kafkaTemplate.send(ATTENDANCE_TOPIC, attendance);

        return attendance;
    }

    public Attendance checkOut(Long employeeId) {
        LocalDateTime now = LocalDateTime.now();
        Attendance attendance = attendanceRepository.findByEmployeeIdAndCheckInTimeBetween(
                employeeId,
                now.toLocalDate().atStartOfDay(),
                now
        ).orElseThrow(() -> new RuntimeException("No check-in found for today"));

        attendance.setCheckOutTime(now);
        Duration duration = Duration.between(attendance.getCheckInTime(), attendance.getCheckOutTime());
        attendance.setTotalHours(duration.toHours() + (duration.toMinutesPart() / 60.0));
        attendanceRepository.save(attendance);

        redisTemplate.opsForSet().remove(ONLINE_EMPLOYEES_KEY, employeeId.toString());
        kafkaTemplate.send(ATTENDANCE_TOPIC, attendance);

        return attendance;
    }

    public Attendance getTodayAttendance(Long employeeId) {
        LocalDateTime now = LocalDateTime.now();
        return attendanceRepository.findByEmployeeIdAndCheckInTimeBetween(
                employeeId,
                now.toLocalDate().atStartOfDay(),
                now
        ).orElse(null);
    }

    public List<Attendance> getAttendanceHistory(Long employeeId) {
        return attendanceRepository.findByEmployeeId(employeeId);
    }

    public Page<Attendance> searchAttendance(Specification<Attendance> spec, Pageable pageable) {
        return attendanceRepository.findAll(spec, pageable);
    }
}
