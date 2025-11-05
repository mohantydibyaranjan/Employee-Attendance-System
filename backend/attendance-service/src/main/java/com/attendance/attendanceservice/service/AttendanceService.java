package com.attendance.attendanceservice.service;

import com.attendance.attendanceservice.client.EmployeeServiceClient;
import com.attendance.common.dto.ApiResponse;
import com.attendance.common.dto.EmployeeDto;
import com.attendance.common.entity.Attendance;
import com.attendance.attendanceservice.repository.AttendanceRepository;
import com.attendance.common.exception.InvalidOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private EmployeeServiceClient employeeServiceClient;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String ATTENDANCE_TOPIC = "attendance-topic";
    private static final String ONLINE_EMPLOYEES_KEY = "online_employees";

    public Attendance checkIn(Long employeeId, String authToken) {
        ApiResponse<EmployeeDto> employeeResponse = employeeServiceClient.getEmployeeById(authToken, employeeId);
        if (Objects.isNull(employeeResponse) || Objects.isNull(employeeResponse.getData())) {
            throw new InvalidOperationException("Employee not found");
        }
        EmployeeDto employee = employeeResponse.getData();
        Attendance attendance = new Attendance();
        attendance.setEmployeeId(employeeId);
        attendance.setEmail(employee.getEmail());
        attendance.setName(employee.getName());
        LocalDateTime checkInTime = LocalDateTime.now();
        attendance.setCheckInTime(checkInTime);

        LocalTime checkInTimeOfDay = checkInTime.toLocalTime();
        if (checkInTimeOfDay.isAfter(LocalTime.of(9, 30))) {
            attendance.setStatus("LATE");
        } else {
            attendance.setStatus("PRESENT");
        }

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
        ).orElseThrow(() -> new InvalidOperationException("No check-in found for today"));

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
