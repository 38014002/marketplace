package com.marketplace.ms_user.controller;

import com.marketplace.ms_user.dto.ApiResponse;
import com.marketplace.ms_user.dto.LoginDto;
import com.marketplace.ms_user.dto.UserRegistrationDto;
import com.marketplace.ms_user.model.User;
import com.marketplace.ms_user.service.UserService;
import com.marketplace.ms_user.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtService;
    private final PasswordEncoder passwordEncoder;

    // 1. LISTAR USUARIOS
    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> listar() {
        log.info("Petición para listar todos los usuarios");
        List<User> usuarios = userService.listarTodos();
        return ResponseEntity.ok(ApiResponse.<List<User>>builder()
                .success(true)
                .message("Usuarios listados correctamente")
                .data(usuarios)
                .build());
    }

    // 2. REGISTRAR USUARIO (Crea en DB y notifica a ms-notification)
    @PostMapping
    public ResponseEntity<ApiResponse<User>> guardar(@Valid @RequestBody UserRegistrationDto dto) {
        log.info("Registrando nuevo usuario: {}", dto.getUsername());
        User nuevoUsuario = userService.registrar(dto);
        return ResponseEntity.status(201).body(ApiResponse.<User>builder()
                .success(true)
                .message("Usuario creado con éxito y notificación enviada")
                .data(nuevoUsuario)
                .build());
    }

    // 3. LOGIN (Dentro de ms-user, opcional si usas ms-auth)
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginDto loginDto) {
        log.info("Intento de login para usuario: {}", loginDto.getUsername());
        User user = userService.buscarPorUsername(loginDto.getUsername());

        if (user != null && passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
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

    /**
     * 4. VALIDATE (Endpoint especial para comunicación entre microservicios)
     * Este endpoint es invocado por ms-auth vía WebClient.
     * No usa ApiResponse para facilitar el mapeo directo en el otro micro.
     */
    @GetMapping("/validate/{username}")
    public ResponseEntity<User> validateForAuth(@PathVariable String username) {
        log.info("ms-auth solicitando validación para usuario: {}", username);
        User user = userService.buscarPorUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
}