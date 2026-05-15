package com.marketplace.ms_search.ms_search.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitamos CSRF porque para APIs REST con tokens o comunicación entre
                // microservicios no es necesario
                .csrf(csrf -> csrf.disable())

                // Configuramos los permisos de las rutas
                .authorizeHttpRequests(auth -> auth
                        // Permitimos todo lo que esté bajo /api/search/ para que Producto pueda
                        // sincronizar
                        .requestMatchers("/api/search/**").permitAll()

                        // Cualquier otra ruta requerirá autenticación
                        .anyRequest().authenticated())

                // Deshabilitamos el formulario de login por defecto de Spring Security
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}