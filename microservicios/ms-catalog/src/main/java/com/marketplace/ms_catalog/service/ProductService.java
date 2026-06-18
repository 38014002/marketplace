package com.marketplace.ms_catalog.service;

import com.marketplace.ms_catalog.dto.ApiResponse;
import com.marketplace.ms_catalog.dto.ProductRequestDTO;
import com.marketplace.ms_catalog.dto.StockDto;
import com.marketplace.ms_catalog.exception.RecursoNoEncontradoException;
import com.marketplace.ms_catalog.model.Product;
import com.marketplace.ms_catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final WebClient inventoryWebClient;
    private final WebClient searchWebClient;

    // 1. LISTAR TODO
    public List<Product> listarTodos() {
        return productRepository.findAll();
    }

    // 2. DETALLE CON STOCK (Usando tu propio ApiResponse)
    public ApiResponse<Map<String, Object>> obtenerProductoConStockCompleto(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado ID: " + id));

        // Consultar stock al otro microservicio de forma directa usando tu StockDto
        Integer stock = 0;
        try {
            ApiResponse<StockDto> stockInfo = inventoryWebClient.get()
                    .uri("/{id}", id)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<StockDto>>() {})
                    .block();
            if (stockInfo != null && stockInfo.getData() != null) {
                stock = stockInfo.getData().getStock();
            }
        } catch (Exception e) {
            log.error("ms-inventory no disponible para ID {}", id);
        }

        // Estructuramos la data en un mapa simple
        Map<String, Object> data = new HashMap<>();
        data.put("id", p.getId());
        data.put("name", p.getName());
        data.put("description", p.getDescription());
        data.put("price", p.getPrice());
        data.put("category", p.getCategory());
        data.put("stock", stock);
        data.put("available", stock > 0);

        return ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Producto obtenido con éxito")
                .data(data)
                .build();
    }

    // 3. CREAR
    public Product crearProducto(ProductRequestDTO dto) {
        Product nuevo = new Product();
        nuevo.setName(dto.getName());
        nuevo.setDescription(dto.getDescription());
        nuevo.setPrice(dto.getPrice());
        nuevo.setCategory(dto.getCategory());

        Product guardado = productRepository.save(nuevo);
        sincronizarConSearch(guardado);
        return guardado;
    }

    // 4. ACTUALIZAR
    public Product actualizarProducto(Long id, ProductRequestDTO nuevosDatos) {
        Product existente = productRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado ID: " + id));

        existente.setName(nuevosDatos.getName());
        existente.setDescription(nuevosDatos.getDescription());
        existente.setPrice(nuevosDatos.getPrice());
        existente.setCategory(nuevosDatos.getCategory());

        Product actualizado = productRepository.save(existente);
        sincronizarConSearch(actualizado);
        return actualizado;
    }

    // 5. ELIMINAR
    public void eliminarProducto(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Producto no encontrado ID: " + id);
        }
        productRepository.deleteById(id);
    }

    // SINCRONIZACIÓN CON MS-SEARCH
    private void sincronizarConSearch(Product producto) {
        searchWebClient.post().uri("/sync").bodyValue(producto).retrieve().bodyToMono(Void.class)
                .subscribe(res -> log.info("Sincronizado"), err -> log.error("Error sincronización"));
    }
}