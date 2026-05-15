package com.marketplace.ms_order.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Cliente para comunicarse con el Inventario (Puerto 8084)
    @Bean
    public WebClient inventoryWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8084/api/inventario")
                .build();
    }

    // Cliente para comunicarse con el Catálogo (Puerto 8082)
    @Bean
    public WebClient catalogWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8082/api/catalog")
                .build();
    }

    // Cliente para enviar notificaciones de compra (Puerto 8086)
    @Bean
    public WebClient notificationWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8086/api/notifications")
                .build();
    }
}