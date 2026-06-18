package com.marketplace.ms_order.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class PaymentClient {

    private final WebClient webClient;

    public PaymentClient(@Qualifier("paymentWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public String processPayment(Long orderId, String token) {
        return webClient
                .post()
                .uri("/process/{orderId}", orderId)
                .headers(headers -> applyAuth(headers, token))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private void applyAuth(HttpHeaders headers, String token) {
        if (token != null && !token.isBlank()) {
            headers.setBearerAuth(token);
        }
    }
}
