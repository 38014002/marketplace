package com.marketplace.product_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient catalogWebClient(
            @Value("${services.catalog.url:http://localhost:8085/api/catalog}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    public WebClient inventoryWebClient(
            @Value("${services.inventory.url:http://localhost:8084/api/inventory}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }
}
