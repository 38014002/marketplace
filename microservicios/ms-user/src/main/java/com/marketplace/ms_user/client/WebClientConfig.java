package com.marketplace.ms_user.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Bean para enviarle alertas o bienvenidas al micro de notificaciones
    @Bean
    public WebClient notificationWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8090/api/notifications")
                .build();
    }
}