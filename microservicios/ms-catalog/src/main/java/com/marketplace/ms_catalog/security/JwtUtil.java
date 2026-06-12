package com.marketplace.ms_catalog.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class JwtUtil {

    private final SecretKey key;

    // Inyectamos la clave desde application.properties
    public JwtUtil(@Value("${jwt.secret}") String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extrae todos los datos (claims) del token.
     */
    public Claims extraerTodoElContenido(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extrae el nombre de usuario (subject) del token
     */
    public String extraerUsername(String token) {
        return extraerTodoElContenido(token).getSubject();
    }

    /**
     * Extrae la lista de roles del token.
     */
    @SuppressWarnings("unchecked")
    public List<String> extraerRoles(String token) {
        return extraerTodoElContenido(token).get("roles", List.class);
    }
}