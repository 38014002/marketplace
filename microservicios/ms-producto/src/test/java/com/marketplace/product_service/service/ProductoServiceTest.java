package com.marketplace.product_service.service;

import com.marketplace.product_service.client.dto.CatalogProduct;
import com.marketplace.product_service.client.dto.ServiceApiResponse;
import com.marketplace.product_service.client.dto.StockInfo;
import com.marketplace.product_service.dto.ProductoDto;
import com.marketplace.product_service.dto.ProductoResponse;
import com.marketplace.product_service.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
        when(catalogWebClient.get().retrieve().bodyToMono(CatalogProduct[].class))
                .thenReturn(Mono.empty());

        assertTrue(productoService.listarTodos().isEmpty());
    }

    @Test
    void listarTodos_conProductos_debeMapearStockCeroSiInventarioFalla() {
        CatalogProduct producto = catalogProduct(1L, "Mouse");
        when(catalogWebClient.get().retrieve().bodyToMono(CatalogProduct[].class))
                .thenReturn(Mono.just(new CatalogProduct[]{producto}));
        when(inventoryWebClient.get().uri("/{productId}", 1L).retrieve()
                .bodyToMono(ArgumentMatchers.<ParameterizedTypeReference<ServiceApiResponse<StockInfo>>>any()))
                .thenReturn(Mono.error(new RuntimeException("inventory down")));

        List<ProductoResponse> result = productoService.listarTodos();

        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getStock());
    }

    @Test
    void buscarPorId_debeRetornarProductoAgregado() {
        ServiceApiResponse<Map<String, Object>> response = new ServiceApiResponse<>();
        response.setData(Map.of(
                "id", 1,
                "name", "Teclado",
                "description", "Mecanico",
                "price", 99.0,
                "category", "Accesorios",
                "stock", 3,
                "available", true
        ));
        when(catalogWebClient.get().uri("/{id}", 1L).retrieve()
                .bodyToMono(ArgumentMatchers.<ParameterizedTypeReference<ServiceApiResponse<Map<String, Object>>>>any()))
                .thenReturn(Mono.just(response));

        ProductoResponse result = productoService.buscarPorId(1L);

        assertEquals("Teclado", result.getName());
        assertEquals(3, result.getStock());
    }

    @Test
    void buscarPorId_cuandoNoExiste_debeLanzarExcepcion() {
        when(catalogWebClient.get().uri("/{id}", 99L).retrieve()
                .bodyToMono(ArgumentMatchers.<ParameterizedTypeReference<ServiceApiResponse<Map<String, Object>>>>any()))
                .thenReturn(Mono.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> productoService.buscarPorId(99L));
    }

    @Test
    void crear_sinStock_debeCrearSoloEnCatalogo() {
        ProductoDto dto = productoDto("Monitor", 0);
        CatalogProduct creado = catalogProduct(10L, "Monitor");

        stubPostCatalog(creado);

        ProductoResponse result = productoService.crear(dto);

        assertEquals(10L, result.getId());
        assertEquals(0, result.getStock());
    }

    @Test
    void crear_sinRespuestaDeCatalogo_debeLanzarExcepcion() {
        ProductoDto dto = productoDto("X", 0);
        stubPostCatalogEmpty();

        assertThrows(RuntimeException.class, () -> productoService.crear(dto));
    }

    @Test
    void actualizar_debeActualizarProducto() {
        ProductoDto dto = productoDto("Actualizado", null);
        CatalogProduct actualizado = catalogProduct(1L, "Actualizado");
        stubPutCatalog(1L, actualizado);

        ProductoResponse result = productoService.actualizar(1L, dto);

        assertEquals("Actualizado", result.getName());
    }

    @Test
    void eliminar_debeEliminarEnCatalogo() {
        stubDelete(catalogWebClient, "/{id}", 1L, ResponseEntity.noContent().build());
        stubDelete(inventoryWebClient, "/{productId}", 1, ResponseEntity.noContent().build());

        assertDoesNotThrow(() -> productoService.eliminar(1L));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void stubPostCatalog(CatalogProduct product) {
        WebClient.RequestBodyUriSpec postSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec bodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(catalogWebClient.post()).thenReturn(postSpec);
        when(postSpec.headers(any(Consumer.class))).thenReturn(bodySpec);
        when(bodySpec.bodyValue(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CatalogProduct.class)).thenReturn(Mono.just(product));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void stubPostCatalogEmpty() {
        WebClient.RequestBodyUriSpec postSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec bodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(catalogWebClient.post()).thenReturn(postSpec);
        when(postSpec.headers(any(Consumer.class))).thenReturn(bodySpec);
        when(bodySpec.bodyValue(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CatalogProduct.class)).thenReturn(Mono.empty());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void stubPutCatalog(long id, CatalogProduct product) {
        WebClient.RequestBodyUriSpec putSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec bodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(catalogWebClient.put()).thenReturn(putSpec);
        when(putSpec.uri("/{id}", id)).thenReturn(bodySpec);
        when(bodySpec.headers(any(Consumer.class))).thenReturn(bodySpec);
        when(bodySpec.bodyValue(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CatalogProduct.class)).thenReturn(Mono.just(product));
        when(inventoryWebClient.get().uri("/{productId}", id).retrieve()
                .bodyToMono(ArgumentMatchers.<ParameterizedTypeReference<ServiceApiResponse<StockInfo>>>any()))
                .thenReturn(Mono.empty());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void stubDelete(WebClient client, String path, Object id, ResponseEntity<Void> response) {
        WebClient.RequestHeadersUriSpec deleteSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(client.delete()).thenReturn(deleteSpec);
        when(deleteSpec.uri(path, id)).thenReturn(headersSpec);
        when(headersSpec.headers(any(Consumer.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.just(response));
    }

    private CatalogProduct catalogProduct(Long id, String name) {
        CatalogProduct p = new CatalogProduct();
        p.setId(id);
        p.setName(name);
        p.setDescription("desc");
        p.setPrice(BigDecimal.valueOf(100));
        p.setCategory("Cat");
        return p;
    }

    private ProductoDto productoDto(String name, Integer stock) {
        ProductoDto dto = new ProductoDto();
        dto.setName(name);
        dto.setDescription("desc");
        dto.setPrice(BigDecimal.valueOf(100));
        dto.setCategory("Cat");
        dto.setStock(stock);
        return dto;
    }
}
