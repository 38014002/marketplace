package com.marketplace.ms_review.ms_review.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.marketplace.ms_review.ms_review.dto.ApiResponse;
import com.marketplace.ms_review.ms_review.dto.ReviewResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentClient {

    private final WebClient webClient;

    private final String BASE_URL = "http://localhost:8083/api/pagos/";

    public ReviewResponse obtenerProducto(Integer id, String token) {

        ApiResponse<ReviewResponse> response = webClient.get()
                .uri(BASE_URL + id)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<ApiResponse<ReviewResponse>>() {})
                .block();

        return response != null ? response.getData() : null;
    }
}
