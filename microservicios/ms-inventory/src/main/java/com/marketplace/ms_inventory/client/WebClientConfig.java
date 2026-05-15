package com.marketplace.ms_inventory.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Bean para comunicarse con el Catálogo (Puerto 8082)
    // Útil para verificar si un producto existe antes de actualizar su inventario
    @Bean
    public WebClient catalogWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8082/api/catalog")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    // Bean genérico por si necesitas hablar con otro micro futuro
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}