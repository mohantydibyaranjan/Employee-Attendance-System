package com.attendance.notificationservice.consumer;

import com.attendance.common.entity.Attendance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "attendance-topic", groupId = "notification-group")
    public void consume(Attendance attendance) {
        if (attendance.getCheckInTime().toLocalTime().isAfter(LocalTime.of(9, 30))) {
            logger.warn("Late check-in detected for employee: " + attendance.getEmployeeId());
        }
    }
}
