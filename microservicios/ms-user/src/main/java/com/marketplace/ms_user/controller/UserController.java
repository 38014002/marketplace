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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Usuarios", description = "Endpoints para la gestión, ciclo de vida, login y validación de usuarios")
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtService;
    private final PasswordEncoder passwordEncoder;

    // 1. LISTAR USUARIOS - RESTRINGIDO: SOLO ADMIN
    @Operation(summary = "Listar todos los usuarios registrados (Solo ADMIN)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuarios listados correctamente")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> listar() {
        log.info("Petición para listar todos los usuarios - Operación protegida para ADMIN");
        List<User> usuarios = userService.listarTodos();
        return ResponseEntity.ok(ApiResponse.<List<User>>builder()
                .success(true)
                .message("Usuarios listados correctamente")
                .data(usuarios)
                .build());
    }

    // 2. REGISTRAR USUARIO - PÚBLICO
    @Operation(summary = "Registrar un nuevo usuario en el sistema")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Usuario creado con éxito y notificación enviada")
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

    // 3. LOGIN - PÚBLICO
    @Operation(summary = "Autenticar credenciales de usuario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login exitoso")
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

    // 4. VALIDATE - INTERNO (Invocado por ms-auth vía WebClient)
    @Operation(summary = "Validar usuario para ms-auth (Uso Interno)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario encontrado y activo")
    @GetMapping("/validate/{username}")
    public ResponseEntity<User> validateForAuth(@PathVariable String username) {
        log.info("ms-auth solicitando validación para usuario: {}", username);
        User user = userService.buscarPorUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    // 5. ACTUALIZAR USUARIO - RESTRINGIDO: SOLO ADMIN
    @Operation(summary = "Actualizar un usuario existente (Solo ADMIN)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario actualizado con éxito")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody UserRegistrationDto dto) {
        log.info("Petición para actualizar usuario con ID: {} - Operación protegida para ADMIN", id);

        User usuarioActualizado = userService.actualizar(id, dto);

        return ResponseEntity.ok(ApiResponse.<User>builder()
                .success(true)
                .message("Usuario actualizado con éxito")
                .data(usuarioActualizado)
                .build());
    }

    // 6. ELIMINAR USUARIO - RESTRINGIDO: SOLO ADMIN
    @Operation(summary = "Eliminar un usuario por su ID (Solo ADMIN)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario eliminado con éxito")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        log.info("Petición para eliminar usuario con ID: {} - Operación protegida para ADMIN", id);

        userService.eliminar(id);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Usuario eliminado correctamente")
                .build());
    }
}