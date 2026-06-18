package com.marketplace.ms_catalog.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                // 1. Extraer el nombre de usuario de forma directa
                String username = jwtUtil.extraerUsername(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // 2. Extraer roles de forma segura (soportando listas o strings individuales)
                    List<String> rawRoles = new ArrayList<>();
                    Claims claims = jwtUtil.extraerTodoElContenido(token);

                    Object rolesClaim = claims.get("roles");
                    if (rolesClaim instanceof List<?> list) {
                        list.forEach(role -> rawRoles.add(role.toString()));
                    } else if (rolesClaim instanceof String role) {
                        rawRoles.add(role);
                    }

                    if (rawRoles.isEmpty()) {
                        String singleRole = claims.get("role", String.class);
                        if (singleRole != null) {
                            rawRoles.add(singleRole);
                        }
                    }

                    // 3. Normalizar todos los roles agregando el prefijo ROLE_ si no lo tienen
                    List<SimpleGrantedAuthority> authorities = rawRoles.stream()
                            .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    // 4. Autenticar al usuario en el contexto de Spring Security
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities);

                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.info("Usuario {} autenticado con éxito en ms-catalog. Roles: {}", username, authorities);
                }

            } catch (Exception e) {
                log.error("Error al procesar el token JWT: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}