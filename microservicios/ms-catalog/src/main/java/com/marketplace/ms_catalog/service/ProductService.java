package com.marketplace.ms_catalog.service;

import com.marketplace.ms_catalog.dto.StockDto;
import com.marketplace.ms_catalog.exception.RecursoNoEncontradoException;
import com.marketplace.ms_catalog.model.Product;
import com.marketplace.ms_catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final WebClient inventoryWebClient; // Configurado en WebClientConfig

    public Map<String, Object> obtenerProductoConStockCompleto(Long id) {
        // 1. Buscamos los datos comerciales en la DB de ms_catalog
        Product producto = productRepository.findById(id)
                .orElseThrow(
                        () -> new RecursoNoEncontradoException("Producto no encontrado en catálogo con ID: " + id));

        try {
            // 2. Llamamos al microservicio de Inventario (Puerto 8084)
            // .block() se usa aquí porque este microservicio no es 100% reactivo
            StockDto stockInfo = inventoryWebClient.get()
                    .uri("/{id}", id)
                    .retrieve()
                    .bodyToMono(StockDto.class)
                    .block();

            // 3. Mezclamos ambos mundos en una respuesta ordenada
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("id", producto.getId());
            response.put("nombre", producto.getName());
            response.put("descripcion", producto.getDescription());
            response.put("precio", producto.getPrice());
            response.put("categoria", producto.getCategory());

            // Datos que vienen del WebClient (No están en nuestra DB)
            response.put("stockActual", stockInfo != null ? stockInfo.getStock() : 0);
            response.put("disponible", stockInfo != null && stockInfo.getStock() > 0);

            return response;

        } catch (Exception e) {
            // Si el microservicio de inventario falla, devolvemos lo que tenemos con un
            // aviso
            Map<String, Object> fallback = new LinkedHashMap<>();
            fallback.put("id", producto.getId());
            fallback.put("nombre", producto.getName());
            fallback.put("precio", producto.getPrice());
            fallback.put("info_inventario", "No disponible en este momento");
            return fallback;
        }
    }
}