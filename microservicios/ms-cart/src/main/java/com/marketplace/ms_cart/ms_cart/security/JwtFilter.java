package com.marketplace.ms_cart.ms_cart.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.equals("/swagger-ui.html");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = req.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    if (jwtUtil.esValido(token)) {
                        String user = jwtUtil.obtenerUsuario(token);
                        String role = jwtUtil.obtenerRole(token);
                        if (role != null && !role.startsWith("ROLE_")) {
                            role = "ROLE_" + role;
                        }

                        var auth = new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                List.of(new SimpleGrantedAuthority(role))
                        );

                        SecurityContextHolder.getContext().setAuthentication(auth);

                        log.info("Usuario autenticado",
                                keyValue("user", user),
                                keyValue("role", role)
                        );
                    }
                } catch (Exception e) {
                    SecurityContextHolder.clearContext();
                    log.warn("Token invalido o expirado");
                }
            }
        }

        chain.doFilter(req, res);
    }
}
