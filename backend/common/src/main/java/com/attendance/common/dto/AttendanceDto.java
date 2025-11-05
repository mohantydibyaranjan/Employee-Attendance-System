package com.attendance.common.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendanceDto {
    private Long id;
    private Long employeeId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Double totalHours;
    private String status;
}
