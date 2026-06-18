package com.marketplace.product_service.service;

import com.marketplace.product_service.client.dto.CatalogProduct;
import com.marketplace.product_service.dto.ProductoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock(answer = RETURNS_DEEP_STUBS) private WebClient catalogWebClient;
    @Mock(answer = RETURNS_DEEP_STUBS) private WebClient inventoryWebClient;

    private ProductoService productoService;

    @BeforeEach
    void setUp() {
        productoService = new ProductoService(catalogWebClient, inventoryWebClient);
    }

    @Test
    void listarTodos_cuandoCatalogoVacio_debeRetornarListaVacia() {
        // Given
        when(catalogWebClient.get().retrieve().bodyToMono(CatalogProduct[].class))
                .thenReturn(Mono.empty());

        // When
        List<?> result = productoService.listarTodos();

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void crear_sinRespuestaDeCatalogo_debeLanzarExcepcion() {
        // Given
        ProductoDto dto = new ProductoDto();
        dto.setName("X");
        dto.setPrice(new BigDecimal("10"));
        dto.setCategory("Y");

        when(catalogWebClient.post().headers(any()).bodyValue(any()).retrieve()
                .bodyToMono(CatalogProduct.class)).thenReturn(Mono.empty());

        // When / Then
        assertThrows(RuntimeException.class, () -> productoService.crear(dto));
    }
}
