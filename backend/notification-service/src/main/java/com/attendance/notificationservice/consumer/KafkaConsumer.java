package com.attendance.notificationservice.consumer;

import com.attendance.common.entity.Attendance;
import com.attendance.notificationservice.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    private EmailService emailService;

    @Value("${admin.email}")
    private String adminEmail;

    @KafkaListener(topics = "attendance-topic", groupId = "notification-group")
    public void consume(Attendance attendance) {
        if ("LATE".equals(attendance.getStatus())) {
            logger.info("Late check-in detected for employee: {}, sending notification...", attendance.getName());

            String employeeEmail = attendance.getEmail();
            String subject = "Late Check-in Alert";
            String checkInTime = attendance.getCheckInTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String body = String.format("Dear %s,\n\nThis is an alert that you have checked in late today at %s.\n\nPlease ensure you arrive on time in the future.\n\nRegards,\nThe System",
                    attendance.getName(), checkInTime);

            // Send email to the employee
            emailService.sendEmail(employeeEmail, subject, body);
            logger.info("Sent late check-in alert to employee: {}", employeeEmail);

            // Send email to the admin
            emailService.sendEmail(adminEmail, subject, body);
            logger.info("Sent late check-in alert to admin: {}", adminEmail);
        }
    }
}
