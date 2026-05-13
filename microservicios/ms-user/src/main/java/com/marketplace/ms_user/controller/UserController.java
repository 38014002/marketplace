package com.marketplace.ms_user.controller;

import com.marketplace.ms_user.dto.ApiResponse;
import com.marketplace.ms_user.dto.LoginDto;
import com.marketplace.ms_user.dto.UserRegistrationDto;
import com.marketplace.ms_user.model.User;
import com.marketplace.ms_user.service.UserService;
import com.marketplace.ms_user.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> listar() {
        List<User> usuarios = userService.listarTodos();
        return ResponseEntity.ok(ApiResponse.<List<User>>builder()
                .success(true)
                .message("Usuarios listados correctamente")
                .data(usuarios)
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginDto loginDto) {
        User user = userService.buscarPorUsername(loginDto.getUsername());

        if (passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            String token = jwtService.generarToken(user.getUsername(), user.getRole());
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Login exitoso")
                    .data(token)
                    .build());
        }

        return ResponseEntity.status(401).body(ApiResponse.<String>builder()
                .success(false)
                .message("Credenciales incorrectas")
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> guardar(@Valid @RequestBody UserRegistrationDto dto) {
        User nuevoUsuario = userService.registrar(dto);
        return ResponseEntity.status(201).body(ApiResponse.<User>builder()
                .success(true)
                .message("Usuario creado con éxito")
                .data(nuevoUsuario)
                .build());
    }
}