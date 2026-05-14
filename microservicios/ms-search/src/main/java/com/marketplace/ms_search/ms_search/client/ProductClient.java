package com.marketplace.ms_search.ms_search.client;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.marketplace.ms_search.ms_search.dto.ProductResponseDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductClient {

    private final WebClient webClient;

    private final String BASE_URL =
            "http://localhost:8081/api/productos";
    public List<ProductResponseDto> buscarPorNombre(String query) {

    return webClient.get()
            .uri(BASE_URL + "/search?name=" + query)
            .retrieve()
            .bodyToFlux(ProductResponseDto.class)
            .collectList()
            .block();
}
}