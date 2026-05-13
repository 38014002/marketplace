package com.marketplace.ms_catalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient inventoryWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8084/api/inventario") // <--- Debe decir "inventario"
                .build();
    }
}