package com.attendance.attendanceservice.controller;

import com.attendance.attendanceservice.dto.AttendanceDto;
import com.attendance.common.entity.Attendance;
import com.attendance.attendanceservice.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/checkin")
    public ResponseEntity<Attendance> checkIn(@RequestBody AttendanceDto attendanceDto) {
        return ResponseEntity.ok(attendanceService.checkIn(attendanceDto.getEmployeeId()));
    }

    @PostMapping("/checkout")
    public ResponseEntity<Attendance> checkOut(@RequestBody AttendanceDto attendanceDto) {
        return ResponseEntity.ok(attendanceService.checkOut(attendanceDto.getEmployeeId()));
    }

    @GetMapping("/today/{employeeId}")
    public ResponseEntity<Attendance> getTodayAttendance(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceService.getTodayAttendance(employeeId));
    }

    @GetMapping("/history/{employeeId}")
    public ResponseEntity<List<Attendance>> getAttendanceHistory(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceService.getAttendanceHistory(employeeId));
    }
}
