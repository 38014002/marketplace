package com.marketplace.ms_auth.service;

import com.marketplace.ms_auth.model.Usuario;
import com.marketplace.ms_auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final WebClient userWebClient;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String login(String username, String password) {
        log.info("Intentando validar usuario {} en ms-user", username);

        try {
            // 1. Llamada al microservicio ms-user
            Usuario user = userWebClient.get()
                    .uri("/validate/{username}", username)
                    .retrieve()
                    // Si ms-user devuelve 404, retornamos un Mono vacío
                    .onStatus(status -> status.is4xxClientError(), response -> Mono.empty())
                    .bodyToMono(Usuario.class)
                    .block(); // Bloqueamos para obtener el resultado síncrono

            if (user == null) {
                return "Error: Usuario no encontrado en el sistema";
            }

            // 2. Validar contraseña con BCrypt
            if (passwordEncoder.matches(password, user.getPassword())) {
                log.info("Contraseña correcta para usuario {}", username);
                // 3. Generar el Token
                return jwtUtil.generarToken(user.getUsername(), user.getRole());
            } else {
                return "Error: Contraseña incorrecta";
            }

        } catch (Exception e) {
            log.error("Error en la comunicación con ms-user: {}", e.getMessage());
            return "Error: No se pudo conectar con el servicio de usuarios";
        }
    }
}