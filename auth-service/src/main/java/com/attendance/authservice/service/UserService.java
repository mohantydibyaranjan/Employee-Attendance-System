package com.attendance.authservice.service;

import com.attendance.authservice.dto.UserDto;
import com.attendance.authservice.entity.User;
import com.attendance.authservice.repository.UserRepository;
import com.attendance.authservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public User register(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(userDto.getRole());
        return userRepository.save(user);
    }

    public String login(UserDto userDto) {
        User user = userRepository.findByUsername(userDto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            return jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getId());
        }
        throw new RuntimeException("Invalid password");
    }
}
