package com.attendance.reportservice.consumer;

import com.attendance.common.entity.Attendance;
import com.attendance.reportservice.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    @Autowired
    private ReportService reportService;

    @KafkaListener(topics = "attendance-topic", groupId = "report-group")
    public void consume(Attendance attendance) {
        reportService.generateReport(attendance);
    }
}
