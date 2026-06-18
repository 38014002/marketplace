package com.example.ms_auth.controller;

import com.example.ms_auth.dto.*;
import com.example.ms_auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticación", description = "Registro, login y refresh de tokens JWT")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService service;

    @Operation(summary = "Registrar nuevo usuario", description = "Crea credenciales y devuelve access + refresh token")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario registrado correctamente")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest req) {
        log.info("POST /auth/register - usuario: {}", req.getUsername());
        AuthResponse res = service.register(req);
        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("Usuario registrado")
                        .data(res)
                        .build());
    }

    @Operation(summary = "Iniciar sesión", description = "Valida credenciales y devuelve tokens JWT")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login exitoso")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest req) {
        log.info("POST /auth/login - usuario: {}", req.getUsername());
        AuthResponse res = service.login(req);
        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("Login exitoso")
                        .data(res)
                        .build());
    }

    @Operation(summary = "Renovar access token", description = "Usa refresh token para obtener un nuevo access token")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token renovado")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody RefreshRequest req) {
        AuthResponse res = service.refresh(req.getRefreshToken());
        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("Token renovado")
                        .data(res)
                        .build());
    }
}
