package com.marketplace.ms_cart.ms_cart.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient productoWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8082/api/productos")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public WebClient userWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8081/api/usuarios")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
