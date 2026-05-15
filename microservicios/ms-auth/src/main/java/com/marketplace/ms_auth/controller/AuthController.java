package com.marketplace.ms_auth.controller;

import com.marketplace.ms_auth.service.AuthService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String resultado = authService.login(request.getUsername(), request.getPassword());

        if (resultado.startsWith("Error")) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", resultado));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "token", resultado));
    }
}

@Data
class LoginRequest {
    private String username;
    private String password;
}