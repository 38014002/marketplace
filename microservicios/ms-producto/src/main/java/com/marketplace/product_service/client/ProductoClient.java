package com.marketplace.product_service.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.marketplace.product_service.dto.ApiResponse;
import com.marketplace.product_service.dto.ProductoResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductoClient {

    private final WebClient webClient;

    private final String BASE_URL = "http://localhost:8083/api/productos/";

    public ProductoResponse obtenerProducto(Integer id, String token) {

        ApiResponse<ProductoResponse> response = webClient.get()
                .uri(BASE_URL + id)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<ApiResponse<ProductoResponse>>() {})
                .block();

        return response != null ? response.getData() : null;
    }
}