package com.attendance.common.util;

import com.attendance.common.dto.ApiResponse;
import com.attendance.common.dto.AttendanceDto;

import java.time.format.DateTimeFormatter;

public class ApiResponseFactory {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    public static ApiResponse<AttendanceDto> createCheckInResponse(AttendanceDto attendance) {
        String message;
        if ("LATE".equals(attendance.getStatus())) {
            message = String.format("You’ve checked in late at %s. Please ensure timely arrival tomorrow.",
                    attendance.getCheckInTime().format(TIME_FORMATTER));
        } else {
            message = String.format("Check-in successful at %s. Have a productive day!",
                    attendance.getCheckInTime().format(TIME_FORMATTER));
        }
        return new ApiResponse<>(true, message, attendance);
    }

    public static ApiResponse<AttendanceDto> createCheckOutResponse(AttendanceDto attendance) {
        String message = String.format("Check-out successful. Total hours worked today: %.2f hours.",
                attendance.getTotalHours());
        return new ApiResponse<>(true, message, attendance);
    }

    public static <T> ApiResponse<T> createMonthlySummaryResponse(T summaryData, String status) {
        String message = "Monthly summary retrieved successfully.";
        if ("GOOD_ATTENDANCE".equals(status)) {
            message += " Excellent attendance this month!";
        } else if ("NEEDS_IMPROVEMENT".equals(status)) {
            message += " Let’s improve next month!";
        }
        return new ApiResponse<>(true, message, summaryData);
    }
}
