package com.marketplace.ms_catalog.service;

import com.marketplace.ms_catalog.dto.ApiResponse;
import com.marketplace.ms_catalog.dto.ProductRequestDTO;
import com.marketplace.ms_catalog.dto.StockDto;
import com.marketplace.ms_catalog.exception.RecursoNoEncontradoException;
import com.marketplace.ms_catalog.model.Product;
import com.marketplace.ms_catalog.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock(answer = RETURNS_DEEP_STUBS) private WebClient inventoryWebClient;
    @Mock(answer = RETURNS_DEEP_STUBS) private WebClient searchWebClient;
    @InjectMocks private ProductService productService;

    @Test
    void listarTodos_debeRetornarProductos() {
        // Given
        when(productRepository.findAll()).thenReturn(List.of(new Product()));

        // When
        List<Product> result = productService.listarTodos();

        // Then
        assertEquals(1, result.size());
    }

    @Test
    void crearProducto_debeGuardarYSincronizar() {
        // Given
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName("Teclado");
        dto.setDescription("Mecanico");
        dto.setPrice(BigDecimal.valueOf(99.0));
        dto.setCategory("Accesorios");

        when(productRepository.save(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        // When
        Product created = productService.crearProducto(dto);

        // Then
        assertEquals("Teclado", created.getName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void eliminarProducto_cuandoNoExiste_debeLanzarExcepcion() {
        // Given
        when(productRepository.existsById(7L)).thenReturn(false);

        // When / Then
        assertThrows(RecursoNoEncontradoException.class, () -> productService.eliminarProducto(7L));
    }

    @Test
    void actualizarProducto_cuandoNoExiste_debeLanzarExcepcion() {
        // Given
        when(productRepository.findById(3L)).thenReturn(Optional.empty());
        ProductRequestDTO dto = new ProductRequestDTO();

        // When / Then
        assertThrows(RecursoNoEncontradoException.class,
                () -> productService.actualizarProducto(3L, dto));
    }

    @Test
    void obtenerProductoConStockCompleto_debeIncluirStock() {
        Product p = new Product();
        p.setId(1L);
        p.setName("Mouse");
        p.setDescription("d");
        p.setPrice(BigDecimal.TEN);
        p.setCategory("Acc");
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));

        StockDto stockDto = new StockDto();
        stockDto.setStock(10);
        ApiResponse<StockDto> apiResp = ApiResponse.<StockDto>builder().success(true).data(stockDto).build();
        when(inventoryWebClient.get().uri("/{id}", 1L).retrieve()
                .bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(apiResp));

        ApiResponse<Map<String, Object>> result = productService.obtenerProductoConStockCompleto(1L);

        assertEquals(10, result.getData().get("stock"));
        assertTrue((Boolean) result.getData().get("available"));
    }

    @Test
    void obtenerProductoConStockCompleto_sinInventario_debeUsarStockCero() {
        Product p = new Product();
        p.setId(1L);
        p.setName("X");
        p.setPrice(BigDecimal.ONE);
        p.setCategory("C");
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));
        when(inventoryWebClient.get().uri("/{id}", 1L).retrieve()
                .bodyToMono(any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("down"));

        ApiResponse<Map<String, Object>> result = productService.obtenerProductoConStockCompleto(1L);

        assertEquals(0, result.getData().get("stock"));
    }

    @Test
    void actualizarProducto_debeGuardarYRetornar() {
        Product existente = new Product();
        existente.setId(1L);
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName("Nuevo");
        dto.setDescription("d");
        dto.setPrice(BigDecimal.TEN);
        dto.setCategory("Cat");
        when(productRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.actualizarProducto(1L, dto);

        assertEquals("Nuevo", result.getName());
    }

    @Test
    void eliminarProducto_debeEliminarCuandoExiste() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.eliminarProducto(1L);

        verify(productRepository).deleteById(1L);
    }
}
