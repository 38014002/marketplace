package com.marketplace.ms_catalog.config;

import com.marketplace.ms_catalog.security.JwtFilter; 
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. CUALQUIERA puede ver el catálogo (Ruta pública para el cliente)
                        .requestMatchers(HttpMethod.GET, "/api/catalog/**").permitAll()

                        // 2. SOLO ADMIN puede modificar el catálogo (Llamadas desde ms-product o Admin
                        // Panel)
                        .requestMatchers(HttpMethod.POST, "/api/catalog/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/catalog/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/catalog/**").hasRole("ADMIN")

                        // 3. Cualquier otra cosa requiere autenticación
                        .anyRequest().authenticated())
                // Añadimos el filtro JWT antes del filtro de usuario/password
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}