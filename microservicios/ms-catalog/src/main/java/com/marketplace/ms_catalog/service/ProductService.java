package com.marketplace.ms_catalog.service;

import com.marketplace.ms_catalog.dto.StockDto;
import com.marketplace.ms_catalog.exception.RecursoNoEncontradoException;
import com.marketplace.ms_catalog.model.Product;
import com.marketplace.ms_catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final WebClient inventoryWebClient;

    public Map<String, Object> obtenerProductoConStockCompleto(Long id) {
        Product producto = productRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado ID: " + id));

        // 1. Detectar si el usuario es ADMIN
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Map<String, Object> response = new LinkedHashMap<>();

        // 2. Información base para TODOS (Cliente y Admin)
        response.put("nombre", producto.getName());
        response.put("descripcion", producto.getDescription());

        // 3. Si es ADMIN, agregamos el resto de la información sensible
        if (isAdmin) {
            response.put("id", producto.getId());
            response.put("precio", producto.getPrice());
            response.put("categoria", producto.getCategory());

            // Intentar traer el stock del microservicio de Inventario
            try {
                // Dentro del método obtenerProductoConStockCompleto
                StockDto stockInfo = inventoryWebClient.get()
                        .uri("/{productId}", id) // Llama a /api/inventario/{id}
                        .retrieve()
                        .bodyToMono(StockDto.class)
                        .block();

                response.put("stockActual", stockInfo != null ? stockInfo.getStock() : 0);
                response.put("disponible", stockInfo != null && stockInfo.getStock() > 0);
            } catch (Exception e) {
                response.put("info_inventario", "Servicio de stock no disponible");
            }
        }

        return response;
    }
}