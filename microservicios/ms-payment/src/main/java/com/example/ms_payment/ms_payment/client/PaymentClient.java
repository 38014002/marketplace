package com.example.ms_payment.ms_payment.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.ms_payment.ms_payment.dto.ApiResponse;
import com.example.ms_payment.ms_payment.dto.PaymentResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentClient {

    private final WebClient webClient;

    private final String BASE_URL = "http://localhost:8083/api/pagos/";

    public PaymentResponse obtenerProducto(Integer id, String token) {

        ApiResponse<PaymentResponse> response = webClient.get()
                .uri(BASE_URL + id)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<ApiResponse<PaymentResponse>>() {})
                .block();

        return response != null ? response.getData() : null;
    }
}
