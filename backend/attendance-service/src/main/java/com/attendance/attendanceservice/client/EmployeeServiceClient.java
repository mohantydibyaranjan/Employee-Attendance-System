package com.attendance.attendanceservice.client;

import com.attendance.common.dto.ApiResponse;
import com.attendance.common.dto.EmployeeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "employee-service")
public interface EmployeeServiceClient {
    @GetMapping("/employee/{id}")
    ApiResponse<EmployeeDto> getEmployeeById(@RequestHeader("Authorization") String authToken, @PathVariable("id") Long id);
}
