package com.marketplace.product_service.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
// IMPORT CORREGIDO SEGÚN TU MODELO
import com.marketplace.product_service.model.Producto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductoClient {

    private final WebClient webClient;

    // URL para sincronizar con MS-SEARCH (8089)
    private final String SEARCH_URL = "http://localhost:8089/api/search/sync";

    /**
     * Sincroniza el producto con el microservicio de búsqueda
     */
    public void enviarASearch(Producto producto) {
        log.info("Sincronizando producto con MS-SEARCH: {}", producto.getNombre());

        webClient.post()
                .uri(SEARCH_URL)
                .bodyValue(producto)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(error -> log.error("❌ Error de conexión con MS-SEARCH (8089): {}", error.getMessage()))
                .subscribe(
                        success -> log.info("✅ Sincronización exitosa con Search"),
                        error -> {
                        });
    }
}