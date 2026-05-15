package com.marketplace.ms_catalog.service;

import com.marketplace.ms_catalog.dto.StockDto;
import com.marketplace.ms_catalog.exception.RecursoNoEncontradoException;
import com.marketplace.ms_catalog.model.Product;
import com.marketplace.ms_catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // Estos nombres deben coincidir con los Beans en WebClientConfig
    private final WebClient inventoryWebClient;
    private final WebClient searchWebClient;

    /**
     * Obtiene un producto y, si el usuario es ADMIN, consulta el stock en
     * ms-inventory
     */
    public Map<String, Object> obtenerProductoConStockCompleto(Long id) {
        Product producto = productRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado ID: " + id));

        // 1. Detectar si el usuario tiene rol ADMIN
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Map<String, Object> response = new LinkedHashMap<>();

        // 2. Información base pública
        response.put("nombre", producto.getName());
        response.put("descripcion", producto.getDescription());

        // 3. Información extendida solo para ADMIN
        if (isAdmin) {
            response.put("id", producto.getId());
            response.put("precio", producto.getPrice());
            response.put("categoria", producto.getCategory());

            // Consulta síncrona a ms-inventory
            try {
                StockDto stockInfo = inventoryWebClient.get()
                        .uri("/{productId}", id)
                        .retrieve()
                        .bodyToMono(StockDto.class)
                        .block(); // Esperamos el resultado porque es necesario para la respuesta

                response.put("stockActual", stockInfo != null ? stockInfo.getStock() : 0);
                response.put("disponible", stockInfo != null && stockInfo.getStock() > 0);
            } catch (Exception e) {
                log.error("Error conectando con ms-inventory: {}", e.getMessage());
                response.put("info_inventario", "Servicio de stock no disponible actualmente");
            }
        }

        return response;
    }

    /**
     * Crea un producto y gatilla la sincronización con el microservicio de búsqueda
     */
    public Product crearProducto(Product producto) {
        // Guardar en la base de datos local (ms-catalog)
        Product nuevoProducto = productRepository.save(producto);

        // Sincronizar con ms-search (Asíncrono para no bloquear la creación)
        sincronizarConSearch(nuevoProducto);

        return nuevoProducto;
    }

    /**
     * Método privado para enviar los datos a ms-search vía POST
     */
    private void sincronizarConSearch(Product producto) {
        searchWebClient.post()
                .uri("/sync") // Ajustar según el endpoint de tu compañero en ms-search
                .bodyValue(producto)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe(
                        result -> log.info("✅ Producto {} sincronizado con ms-search exitosamente", producto.getId()),
                        error -> log.error("❌ Error al sincronizar con ms-search: {}", error.getMessage()));
    }
}