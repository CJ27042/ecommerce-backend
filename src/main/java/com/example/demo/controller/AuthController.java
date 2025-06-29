package com.example.demo.controller;

import com.example.demo.config.JwtUtil;
import com.example.demo.model.AppUser;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository repo;
    @Autowired private JwtUtil jwt;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AppUser user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        repo.save(user);
        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AppUser loginData) {
        AppUser user = repo.findByUsername(loginData.getUsername()).orElseThrow();
        if (new BCryptPasswordEncoder().matches(loginData.getPassword(), user.getPassword())) {
            String token = jwt.generateToken(user.getUsername());
            return ResponseEntity.ok(Map.of("token", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
