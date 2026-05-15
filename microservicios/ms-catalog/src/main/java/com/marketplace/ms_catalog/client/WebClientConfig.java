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
                .baseUrl("http://localhost:8083/api/search") // Puerto sugerido para Search
                .build();
    }

    // Cliente para comunicarse con ms-inventory (Verificar stock)
    @Bean
    public WebClient inventoryWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8084/api/inventory")
                .build();
    }

    // Nota: El "catalogWebClient" aquí dentro no es necesario
    // porque estamos DENTRO de ms-catalog.
    // Pero si este archivo es de ms-order, entonces sí déjalo.
}