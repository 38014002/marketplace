package com.marketplace.product_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Este sirve para el ProductoService que usa .baseUrl().build()
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    // Este es el que te está pidiendo el error para ProductoClient
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }
}