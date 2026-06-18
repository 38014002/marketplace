package com.marketplace.ms_catalog.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient searchWebClient(
            @Value("${services.search.url:http://localhost:8089/api/search}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    public WebClient inventoryWebClient(
            @Value("${services.inventory.url:http://localhost:8084/api/v1/inventory}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }
}