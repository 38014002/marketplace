package com.marketplace.ms_inventory.config;

import com.marketplace.ms_inventory.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. CORREGIDO: Permitir ver el stock general o por ID a todo el mundo (Ruta
                        // v1)
                        .requestMatchers(HttpMethod.GET, "/api/inventory/**").permitAll()

                        // 2. CORREGIDO: Rutas de mutación (POST, PUT, DELETE) exclusivas para
                        // Administradores
                        .requestMatchers(HttpMethod.POST, "/api/inventory/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/inventory/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/inventory/**").hasRole("ADMIN")

                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        .anyRequest().authenticated());

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}