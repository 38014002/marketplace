package com.marketplace.ms_cart.ms_cart.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.marketplace.ms_cart.ms_cart.dto.CartResponse;
import com.marketplace.ms_cart.ms_cart.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CartClient {

    private final WebClient webClient;

    private final String BASE_URL = "http://localhost:8083/api/pagos/";

    public CartResponse obtenerProducto(Integer id, String token) {

        ApiResponse<CartResponse> response = webClient.get()
                .uri(BASE_URL + id)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<ApiResponse<CartResponse>>() {})
                .block();

        return response != null ? response.getData() : null;
    }
}