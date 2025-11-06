package com.attendance.authservice.service;

import com.attendance.authservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResult {
    private User user;
    private String token;
}
