package com.marketplace.ms_inventory.config;

import com.marketplace.ms_inventory.security.JwtAuthenticationFilter;
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
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    // Constructor para inyección de dependencias
    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Desactivamos CSRF ya que usamos JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. Permitir ver el stock a todo el mundo (clientes y otros servicios)
                        .requestMatchers(HttpMethod.GET, "/api/inventario/**").permitAll()

                        // 2. Solo los administradores pueden actualizar o agregar stock
                        .requestMatchers(HttpMethod.POST, "/api/inventario/**").hasRole("ADMIN")

                        // 3. Cualquier otra ruta requiere estar autenticado
                        .anyRequest().authenticated());

        // Añadimos nuestro filtro de JWT antes del filtro estándar de
        // usuario/contraseña
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}