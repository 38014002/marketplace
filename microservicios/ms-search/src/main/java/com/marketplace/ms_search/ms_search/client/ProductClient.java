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

        // Puerto 8081 es correcto para llamar a MS-PRODUCTO
        private final String BASE_URL = "http://localhost:8081/api/productos";

        public List<ProductResponseDto> buscarEnProducto(String query) {
                return webClient.get()
                                // Asegúrate de que en ProductoController tengas un @GetMapping("/buscar")
                                .uri(uriBuilder -> uriBuilder
                                                .path("/buscar")
                                                .queryParam("nombre", query)
                                                .build())
                                .retrieve()
                                .bodyToFlux(ProductResponseDto.class)
                                .collectList()
                                .block(); // Ojo: .block() detiene el hilo, úsalo solo si es necesario
        }
}