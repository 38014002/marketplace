package com.marketplace.ms_catalog.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Cliente para comunicarse con ms-search (Sincronización de productos)
    @Bean
    public WebClient searchWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8089/api/search")
                .build();
    }

    // Cliente para comunicarse con ms-inventory (Verificar stock)
    // DENTRO DE MS-CATALOG (8082)
    @Bean
    public WebClient inventoryWebClient() { // El nombre del método es el ID del Bean
        return WebClient.builder()
                .baseUrl("http://localhost:8084/api/inventory") // Apunta al otro micro
                .build();
    }

}