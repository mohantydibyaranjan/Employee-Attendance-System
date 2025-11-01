package com.attendance.authservice.controller;

import com.attendance.authservice.dto.UserDto;
import com.attendance.authservice.entity.User;
import com.attendance.authservice.service.UserService;
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
    public ResponseEntity<User> register(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.register(userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.login(userDto));
    }
}
