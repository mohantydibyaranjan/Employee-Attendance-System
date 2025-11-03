package com.attendance.notificationservice.service;

import com.attendance.common.entity.Attendance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class KafkaConsumerService {

    @Autowired
    private EmailService emailService;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @KafkaListener(topics = "attendance-topic", groupId = "notification-group")
    public void consume(Attendance attendance) {
        if ("LATE".equals(attendance.getStatus())) {
            String subject = "Late Check-in Alert";
            String body = String.format(
                "Dear Employee,\\n\\nYou have checked in late today at %s. Please try to be on time tomorrow.\\n\\nBest Regards,\\nHR Team",
                attendance.getCheckInTime().format(TIME_FORMATTER)
            );
            emailService.sendEmail(attendance.getEmail(), subject, body);
        }
    }
}
