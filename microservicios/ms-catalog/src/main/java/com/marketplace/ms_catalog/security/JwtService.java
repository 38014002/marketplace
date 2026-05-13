package com.marketplace.ms_catalog.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.nio.charset.StandardCharsets;

@Service
public class JwtService {

    // IMPORTANTE: Esta clave debe ser IGUAL a la que usa el ms-user para generar el
    // token
    private static final String SECRET_KEY = "esta_es_una_clave_secreta_muy_larga_y_segura_1234567890";

    /**
     * Extrae todos los datos (claims) del token.
     * Si el token es inválido o expiró, este método lanzará una excepción
     * que será capturada por el Try-Catch del Filtro.
     */
    public Claims extraerTodoElContenido(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Genera la llave de firma a partir de la cadena de texto secreta.
     */
    private Key getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}