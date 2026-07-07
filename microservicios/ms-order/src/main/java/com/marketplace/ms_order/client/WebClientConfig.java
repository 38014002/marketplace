package com.marketplace.ms_order.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient cartWebClient(
            @Value("${services.cart.url:http://localhost:8086/api/cart}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    public WebClient paymentWebClient(
            @Value("${services.payment.url:http://localhost:8088/api/pagos}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    public WebClient inventoryWebClient(
            @Value("${services.inventory.url:http://localhost:8084/api/inventory}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    public WebClient catalogWebClient(
            @Value("${services.catalog.url:http://localhost:8085/api/catalog}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    public WebClient notificationWebClient(
            @Value("${services.notification.url:http://localhost:8090/api/notifications}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }
}
