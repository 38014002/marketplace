package com.marketplace.ms_order.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Cambiamos los puertos individuales por la ruta unificada del API Gateway
    // (puerto 8080)

    @Bean
    public WebClient cartWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8080/api/cart")
                .build();
    }

    @Bean
    public WebClient paymentWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8080/api/payments")
                .build();
    }

    @Bean
    public WebClient inventoryWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8080/api/inventario")
                .build();
    }

    @Bean
    public WebClient catalogWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8080/api/catalog")
                .build();
    }

    @Bean
    public WebClient notificationWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8080/api/notifications")
                .build();
    }
}