package com.attendance.authservice.controller;

import com.attendance.authservice.dto.AuthRequest;
import com.attendance.authservice.dto.AuthResponse;
import com.attendance.authservice.dto.UserDto;
import com.attendance.authservice.entity.User;
import com.attendance.authservice.service.UserService;
import com.attendance.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "APIs for user registration and login")
public class AuthController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Register a new user", description = "Creates a new user with the given details.")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody UserDto userDto) {
        User registeredUser = userService.register(userDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully", registeredUser));
    }

    @Operation(summary = "Login a user", description = "Authenticates a user and returns a JWT.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest authRequest) {
        String token = userService.login(authRequest);
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", new AuthResponse(token)));
    }
}
