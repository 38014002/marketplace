package com.marketplace.product_service.service;

import com.marketplace.product_service.client.dto.CatalogProduct;
import com.marketplace.product_service.client.dto.CatalogProductRequest;
import com.marketplace.product_service.client.dto.InventoryItem;
import com.marketplace.product_service.client.dto.ServiceApiResponse;
import com.marketplace.product_service.client.dto.StockInfo;
import com.marketplace.product_service.dto.ProductoDto;
import com.marketplace.product_service.dto.ProductoResponse;
import com.marketplace.product_service.exception.RecursoNoEncontradoException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ProductoService {

    private final WebClient catalogWebClient;
    private final WebClient inventoryWebClient;

    public ProductoService(WebClient catalogWebClient, WebClient inventoryWebClient) {
        this.catalogWebClient = catalogWebClient;
        this.inventoryWebClient = inventoryWebClient;
    }

    public List<ProductoResponse> listarTodos() {
        CatalogProduct[] productos = catalogWebClient.get()
                .retrieve()
                .bodyToMono(CatalogProduct[].class)
                .block();

        if (productos == null) {
            return List.of();
        }

        return Arrays.stream(productos)
                .map(this::mapearConStock)
                .toList();
    }

    public ProductoResponse buscarPorId(Long id) {
        try {
            ServiceApiResponse<Map<String, Object>> response = catalogWebClient.get()
                    .uri("/{id}", id)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ServiceApiResponse<Map<String, Object>>>() {})
                    .block();

            if (response == null || response.getData() == null) {
                throw new RecursoNoEncontradoException("Producto no encontrado con ID: " + id);
            }

            return mapearDesdeDetalle(response.getData());
        } catch (WebClientResponseException.NotFound e) {
            throw new RecursoNoEncontradoException("Producto no encontrado con ID: " + id);
        }
    }

    public ProductoResponse crear(ProductoDto dto) {
        CatalogProductRequest request = CatalogProductRequest.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .category(dto.getCategory())
                .build();

        CatalogProduct creado;
        try {
            creado = catalogWebClient.post()
                    .headers(this::aplicarAuth)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(CatalogProduct.class)
                    .block();
        } catch (WebClientResponseException.Forbidden e) {
            throw new AccessDeniedException("ms-catalog rechazó la operación: se requiere token ADMIN");
        }

        if (creado == null || creado.getId() == null) {
            throw new RuntimeException("No se pudo crear el producto en ms-catalog");
        }

        int stock = dto.getStock() != null ? dto.getStock() : 0;
        if (stock > 0) {
            try {
                registrarStock(creado.getId().intValue(), stock);
            } catch (WebClientResponseException.Forbidden e) {
                throw new AccessDeniedException("Producto creado en catálogo, pero ms-inventory rechazó el stock: se requiere token ADMIN");
            }
        }

        return mapearConStock(creado, stock);
    }

    public ProductoResponse actualizar(Long id, ProductoDto dto) {
        CatalogProductRequest request = CatalogProductRequest.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .category(dto.getCategory())
                .build();

        CatalogProduct actualizado = catalogWebClient.put()
                .uri("/{id}", id)
                .headers(this::aplicarAuth)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(CatalogProduct.class)
                .block();

        if (actualizado == null) {
            throw new RecursoNoEncontradoException("Producto no encontrado con ID: " + id);
        }

        if (dto.getStock() != null) {
            ajustarStock(id.intValue(), dto.getStock());
        }

        return mapearConStock(actualizado);
    }

    public void eliminar(Long id) {
        try {
            catalogWebClient.delete()
                    .uri("/{id}", id)
                    .headers(this::aplicarAuth)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new RecursoNoEncontradoException("Producto no encontrado con ID: " + id);
        }

        try {
            inventoryWebClient.delete()
                    .uri("/{productId}", id.intValue())
                    .headers(this::aplicarAuth)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException.NotFound ignored) {
            // El inventario puede no existir para ese producto
        }
    }

    private ProductoResponse mapearConStock(CatalogProduct producto) {
        int stock = consultarStock(producto.getId());
        return mapearConStock(producto, stock);
    }

    private ProductoResponse mapearConStock(CatalogProduct producto, int stock) {
        return ProductoResponse.builder()
                .id(producto.getId())
                .name(producto.getName())
                .description(producto.getDescription())
                .price(producto.getPrice())
                .category(producto.getCategory())
                .stock(stock)
                .available(stock > 0)
                .build();
    }

    private ProductoResponse mapearDesdeDetalle(Map<String, Object> data) {
        return ProductoResponse.builder()
                .id(((Number) data.get("id")).longValue())
                .name((String) data.get("name"))
                .description((String) data.get("description"))
                .price(new java.math.BigDecimal(data.get("price").toString()))
                .category((String) data.get("category"))
                .stock(data.get("stock") != null ? ((Number) data.get("stock")).intValue() : 0)
                .available(data.get("available") != null && (Boolean) data.get("available"))
                .build();
    }

    private int consultarStock(Long productId) {
        try {
            ServiceApiResponse<StockInfo> response = inventoryWebClient.get()
                    .uri("/{productId}", productId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ServiceApiResponse<StockInfo>>() {})
                    .block();

            if (response != null && response.getData() != null && response.getData().getStock() != null) {
                return response.getData().getStock();
            }
        } catch (Exception ignored) {
            // Inventario no disponible
        }
        return 0;
    }

    private void registrarStock(int productId, int stock) {
        InventoryItem item = InventoryItem.builder()
                .productId(productId)
                .stock(stock)
                .build();

        inventoryWebClient.post()
                .headers(this::aplicarAuth)
                .bodyValue(item)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private void ajustarStock(int productId, int stockDeseado) {
        int stockActual = consultarStock((long) productId);
        int delta = stockDeseado - stockActual;

        if (delta == 0) {
            return;
        }

        if (stockActual == 0 && stockDeseado > 0) {
            registrarStock(productId, stockDeseado);
            return;
        }

        inventoryWebClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/actualizar")
                        .queryParam("productId", productId)
                        .queryParam("cantidad", delta)
                        .build())
                .headers(this::aplicarAuth)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private void aplicarAuth(org.springframework.http.HttpHeaders headers) {
        String token = obtenerToken();
        if (token != null) {
            headers.setBearerAuth(token);
        }
    }

    private String obtenerToken() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return null;
        }

        String header = attrs.getRequest().getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
