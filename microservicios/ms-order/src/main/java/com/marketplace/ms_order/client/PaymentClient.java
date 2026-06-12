package com.marketplace.ms_order.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class PaymentClient {

    private final WebClient webClient;

    public PaymentClient(@Qualifier("paymentWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public String processPayment(Long orderId) {
        return webClient
                .post()
                .uri("/process/" + orderId) // Se acopla a: http://localhost:8080/api/payments/process/{id}
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}