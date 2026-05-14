package com.marketplace.ms_catalog.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.nio.charset.StandardCharsets;

@Service
public class JwtUtil {

    private final Key key;

    // Inyectamos la clave desde application.properties
    public JwtUtil(@Value("${jwt.secret}") String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extrae todos los datos (claims) del token.
     * Si el token es inválido o expiró, este método lanzará una excepción.
     */
    public Claims extraerTodoElContenido(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}