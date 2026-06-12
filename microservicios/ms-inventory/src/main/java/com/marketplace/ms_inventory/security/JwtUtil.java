package com.marketplace.ms_inventory.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
public class JwtUtil {

    private final SecretKey key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @SuppressWarnings("unchecked")
    public List<SimpleGrantedAuthority> extractAuthorities(String token) {
        Claims claims = extractAllClaims(token);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        Object rolesObject = claims.get("roles");
        if (rolesObject == null)
            rolesObject = claims.get("authorities");
        if (rolesObject == null)
            rolesObject = claims.get("role");

        if (rolesObject instanceof Collection) {
            ((Collection<?>) rolesObject).forEach(role -> {
                String roleStr = role.toString();
                String formattedRole = roleStr.startsWith("ROLE_") ? roleStr : "ROLE_" + roleStr;
                authorities.add(new SimpleGrantedAuthority(formattedRole));
            });
        } else if (rolesObject instanceof String) {

            String roleStr = (String) rolesObject;
            String formattedRole = roleStr.startsWith("ROLE_") ? roleStr : "ROLE_" + roleStr;
            authorities.add(new SimpleGrantedAuthority(formattedRole));
        }

        return authorities;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}