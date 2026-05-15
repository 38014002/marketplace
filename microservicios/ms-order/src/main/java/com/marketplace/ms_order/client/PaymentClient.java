package com.marketplace.ms_order.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class PaymentClient {

    private final WebClient webClient;

    public String processPayment(Long orderId) {

        return webClient
                .post()
                .uri("http://localhost:8085/api/pagos/process/" + orderId)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}