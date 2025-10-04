package com.example.taskmanager.controller;

import com.example.taskmanager.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body, HttpSession session) {
        String username = body.get("username");
        String password = body.get("password");
        if (userService.validateUser(username, password)) {
            session.setAttribute("user", username);
            return ResponseEntity.ok(Map.of("login", "success"));
        }
        return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("logout", "success"));
    }
}
