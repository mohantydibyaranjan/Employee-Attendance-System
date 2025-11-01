package com.attendance.authservice.controller;

import com.attendance.authservice.dto.UserDto;
import com.attendance.authservice.entity.User;
import com.attendance.authservice.service.UserService;
import com.attendance.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody UserDto userDto) {
        User registeredUser = userService.register(userDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully", registeredUser));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody UserDto userDto) {
        String token = userService.login(userDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", token));
    }
}
