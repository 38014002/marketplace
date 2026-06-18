package com.marketplace.ms_user.controller;

import com.marketplace.ms_user.dto.ApiResponse;
import com.marketplace.ms_user.dto.LoginDto;
import com.marketplace.ms_user.dto.UserRegistrationDto;
import com.marketplace.ms_user.model.User;
import com.marketplace.ms_user.service.UserService;
import com.marketplace.ms_user.security.JwtUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final JwtUtil jwtService;
    private final PasswordEncoder passwordEncoder;

    public UserController(
            UserService userService,
            JwtUtil jwtService,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    // 1. LISTAR USUARIOS - RESTRINGIDO: SOLO ADMIN
    @Operation(summary = "Listar todos los usuarios registrados (Solo ADMIN)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuarios listados correctamente")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> listar() {
        log.info("Petición para listar todos los usuarios - Operación protegida para ADMIN");
        List<User> usuarios = userService.listarTodos();
        return ResponseEntity.ok(ApiResponse.ok("Usuarios listados correctamente", usuarios));
    }

    // 2. REGISTRAR USUARIO - PÚBLICO
    @Operation(summary = "Registrar un nuevo usuario en el sistema")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Usuario creado con éxito y notificación enviada")
    @PostMapping
    public ResponseEntity<ApiResponse<User>> guardar(@Valid @RequestBody UserRegistrationDto dto) {
        log.info("Registrando nuevo usuario: {}", dto.getUsername());
        User nuevoUsuario = userService.registrar(dto);
        return ResponseEntity.status(201).body(ApiResponse.ok("Usuario creado con éxito y notificación enviada", nuevoUsuario));
    }

    // 3. LOGIN - PÚBLICO
    @Operation(summary = "Autenticar credenciales de usuario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login exitoso")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginDto loginDto) {
        log.info("Intento de login para usuario: {}", loginDto.getUsername());
        User user = userService.buscarPorUsernameOptional(loginDto.getUsername()).orElse(null);

        if (user != null && passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            String token = jwtService.generarToken(user.getUsername(), user.getRole());
            return ResponseEntity.ok(ApiResponse.ok("Login exitoso", token));
        }

        return ResponseEntity.status(401).body(ApiResponse.fail("Credenciales incorrectas"));
    }

    // 4. VALIDATE - INTERNO (Invocado por ms-auth vía WebClient)
    @Operation(summary = "Validar usuario para ms-auth (Uso Interno)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario encontrado y activo")
    @GetMapping("/validate/{username}")
    public ResponseEntity<User> validateForAuth(@PathVariable String username) {
        log.info("ms-auth solicitando validación para usuario: {}", username);
        return userService.buscarPorUsernameOptional(username)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
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

        return ResponseEntity.ok(ApiResponse.ok("Usuario actualizado con éxito", usuarioActualizado));
    }

    // 6. ELIMINAR USUARIO - RESTRINGIDO: SOLO ADMIN
    @Operation(summary = "Eliminar un usuario por su ID (Solo ADMIN)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario eliminado con éxito")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        log.info("Petición para eliminar usuario con ID: {} - Operación protegida para ADMIN", id);

        userService.eliminar(id);

        return ResponseEntity.ok(ApiResponse.ok("Usuario eliminado correctamente"));
    }
}