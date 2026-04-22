package com.TgvTeam.SuiviPlus.controller;

import com.TgvTeam.SuiviPlus.entity.User;
import com.TgvTeam.SuiviPlus.service.AuthService;
import com.TgvTeam.SuiviPlus.dto.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // =======================
    // REGISTER
    // =======================
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody User user) {

        authService.register(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Utilisateur créé avec succès");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =======================
    // LOGIN
    // =======================
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {

        String token = authService.login(request.getEmail(), request.getPassword());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("message", "Login successful");

        return ResponseEntity.ok(response);
    }
}