package com.example.smartshop.controller;

import com.example.smartshop.dto.LoginRequest;
import com.example.smartshop.dto.UserDTO;
import com.example.smartshop.service.AuthService;
import com.example.smartshop.util.AuthorizationUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthorizationUtil authorizationUtil;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpSession session) {

        UserDTO user = authService.login(loginRequest.getUsername(), loginRequest.getPassword(), session);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("sessionId", session.getId());
        response.put("user", user);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        authorizationUtil.requireLogin(session);

        authService.logout(session);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logout successful");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpSession session) {
        authorizationUtil.requireLogin(session);

        Long userId = authorizationUtil.getCurrentUserId(session);
        String username = authorizationUtil.getCurrentUsername(session);
        String role = authorizationUtil.getCurrentUserRole(session).name();

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("username", username);
        response.put("role", role);
        response.put("sessionId", session.getId());

        return ResponseEntity.ok(response);
    }
}
