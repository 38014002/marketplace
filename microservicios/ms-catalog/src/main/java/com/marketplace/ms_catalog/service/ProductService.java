package com.marketplace.ms_catalog.service;

import com.marketplace.ms_catalog.dto.StockDto;
import com.marketplace.ms_catalog.exception.RecursoNoEncontradoException;
import com.marketplace.ms_catalog.model.Product;
import com.marketplace.ms_catalog.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.LinkedHashMap;
import java.util.List;
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

            // --- PROPAGACIÓN DEL TOKEN JWT ---
            // Extraemos el token que vino en la petición original para dárselo al
            // Inventario
            String token = getJwtTokenFromRequest();

            try {
                StockDto stockInfo = inventoryWebClient.get()
                        .uri("/{productId}", id)
                        .header("Authorization", "Bearer " + token) // Le pasamos "la llave" al inventario
                        .retrieve()
                        .bodyToMono(StockDto.class)
                        .block();

                response.put("stockActual", stockInfo != null ? stockInfo.getStock() : 0);
                response.put("disponible", stockInfo != null && stockInfo.getStock() > 0);

            } catch (Exception e) {
                log.error("Error conectando con ms-inventory: {}", e.getMessage());
                response.put("info_inventario",
                        "No se pudo obtener el stock (Servicio no disponible o error de permisos)");
            }
        }

        return response;
    }

    /**
     * Extrae el token JWT de la petición actual
     */
    private String getJwtTokenFromRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
        }
        return null;
    }

    /**
     * Crea un producto y gatilla la sincronización con el microservicio de búsqueda
     */
    public Product crearProducto(Product producto) {
        Product nuevoProducto = productRepository.save(producto);
        sincronizarConSearch(nuevoProducto);
        return nuevoProducto;
    }

    /**
     * Método para enviar los datos a ms-search vía POST
     */
    private void sincronizarConSearch(Product producto) {
        searchWebClient.post()
                .uri("/sync")
                .bodyValue(producto)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe(
                        result -> log.info("✅ Producto {} sincronizado con ms-search", producto.getId()),
                        error -> log.error("❌ Error al sincronizar con ms-search: {}", error.getMessage()));
    }

    /**
     * Retorna la lista completa de productos para el Catálogo General
     */
    public List<Product> listarTodos() {
        log.info("Consultando catálogo completo de productos.");
        return productRepository.findAll();
    }

    /**
     * Guarda o actualiza un producto
     */
    public Product guardar(Product producto) {
        Product guardado = productRepository.save(producto);
        log.info("Producto {} guardado. Sincronizando...", guardado.getName());
        sincronizarConSearch(guardado);
        return guardado;
    }
}