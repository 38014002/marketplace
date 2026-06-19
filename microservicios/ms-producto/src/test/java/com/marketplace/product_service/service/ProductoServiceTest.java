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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

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
    void listarTodos_conStockDisponible_debeMapearStock() {
        CatalogProduct producto = catalogProduct(1L, "Mouse");
        when(catalogWebClient.get().retrieve().bodyToMono(CatalogProduct[].class))
                .thenReturn(Mono.just(new CatalogProduct[]{producto}));
        stubInventoryGetStock(1L, 7);

        List<ProductoResponse> result = productoService.listarTodos();

        assertEquals(7, result.get(0).getStock());
        assertTrue(result.get(0).getAvailable());
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
    void buscarPorId_cuandoCatalogoRetorna404_debeLanzarExcepcion() {
        when(catalogWebClient.get().uri("/{id}", 99L).retrieve()
                .bodyToMono(ArgumentMatchers.<ParameterizedTypeReference<ServiceApiResponse<Map<String, Object>>>>any()))
                .thenReturn(Mono.error(notFoundException()));

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
    void crear_conStock_debeRegistrarInventario() {
        ProductoDto dto = productoDto("Monitor", 5);
        CatalogProduct creado = catalogProduct(10L, "Monitor");
        stubPostCatalog(creado);
        stubInventoryPost();

        ProductoResponse result = productoService.crear(dto);

        assertEquals(5, result.getStock());
        assertTrue(result.getAvailable());
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
    void actualizar_cuandoCatalogoRetornaVacio_debeLanzarExcepcion() {
        ProductoDto dto = productoDto("Actualizado", null);
        stubPutCatalogEmpty(1L);

        assertThrows(RecursoNoEncontradoException.class, () -> productoService.actualizar(1L, dto));
    }

    @Test
    void actualizar_conStockNuevo_debeRegistrarInventario() {
        ProductoDto dto = productoDto("Actualizado", 4);
        CatalogProduct actualizado = catalogProduct(1L, "Actualizado");
        stubPutCatalogOnly(1L, actualizado);
        ServiceApiResponse<StockInfo> stockResponse = stockResponse(4);
        when(inventoryWebClient.get().uri("/{productId}", 1L).retrieve()
                .bodyToMono(ArgumentMatchers.<ParameterizedTypeReference<ServiceApiResponse<StockInfo>>>any()))
                .thenReturn(Mono.empty(), Mono.just(stockResponse));
        stubInventoryPost();

        ProductoResponse result = productoService.actualizar(1L, dto);

        assertEquals(4, result.getStock());
        verify(inventoryWebClient).post();
    }

    @Test
    void actualizar_conIncrementoDeStock_debeAjustarInventario() {
        ProductoDto dto = productoDto("Actualizado", 5);
        CatalogProduct actualizado = catalogProduct(1L, "Actualizado");
        stubPutCatalog(1L, actualizado);
        stubInventoryGetStock(1L, 2);
        stubInventoryPutActualizar();

        ProductoResponse result = productoService.actualizar(1L, dto);

        assertEquals(2, result.getStock());
    }

    @Test
    void eliminar_debeEliminarEnCatalogo() {
        stubDelete(catalogWebClient, "/{id}", 1L, ResponseEntity.noContent().build());
        stubDelete(inventoryWebClient, "/{productId}", 1, ResponseEntity.noContent().build());

        assertDoesNotThrow(() -> productoService.eliminar(1L));
    }

    @Test
    void eliminar_cuandoNoExisteEnCatalogo_debeLanzarExcepcion() {
        stubDeleteError(catalogWebClient, "/{id}", 99L, notFoundException());

        assertThrows(RecursoNoEncontradoException.class, () -> productoService.eliminar(99L));
    }

    @Test
    void eliminar_cuandoInventarioNoExiste_debeContinuar() {
        stubDelete(catalogWebClient, "/{id}", 1L, ResponseEntity.noContent().build());
        stubDeleteError(inventoryWebClient, "/{productId}", 1, notFoundException());

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
        stubPutCatalogOnly(id, product);
        when(inventoryWebClient.get().uri("/{productId}", id).retrieve()
                .bodyToMono(ArgumentMatchers.<ParameterizedTypeReference<ServiceApiResponse<StockInfo>>>any()))
                .thenReturn(Mono.empty());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void stubPutCatalogOnly(long id, CatalogProduct product) {
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void stubPutCatalogEmpty(long id) {
        WebClient.RequestBodyUriSpec putSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec bodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(catalogWebClient.put()).thenReturn(putSpec);
        when(putSpec.uri("/{id}", id)).thenReturn(bodySpec);
        when(bodySpec.headers(any(Consumer.class))).thenReturn(bodySpec);
        when(bodySpec.bodyValue(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CatalogProduct.class)).thenReturn(Mono.empty());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void stubInventoryPost() {
        WebClient.RequestBodyUriSpec postSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec bodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(inventoryWebClient.post()).thenReturn(postSpec);
        when(postSpec.headers(any(Consumer.class))).thenReturn(bodySpec);
        when(bodySpec.bodyValue(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.just(ResponseEntity.noContent().build()));
    }

    private void stubInventoryGetStock(long productId, int stock) {
        when(inventoryWebClient.get().uri("/{productId}", productId).retrieve()
                .bodyToMono(ArgumentMatchers.<ParameterizedTypeReference<ServiceApiResponse<StockInfo>>>any()))
                .thenReturn(Mono.just(stockResponse(stock)));
    }

    private ServiceApiResponse<StockInfo> stockResponse(int stock) {
        ServiceApiResponse<StockInfo> response = new ServiceApiResponse<>();
        StockInfo info = new StockInfo();
        info.setStock(stock);
        response.setData(info);
        return response;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void stubInventoryPutActualizar() {
        WebClient.RequestBodyUriSpec putSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec bodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(inventoryWebClient.put()).thenReturn(putSpec);
        when(putSpec.uri(any(Function.class))).thenReturn(bodySpec);
        when(bodySpec.headers(any(Consumer.class))).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.just(ResponseEntity.ok().build()));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void stubDeleteError(WebClient client, String path, Object id, Throwable error) {
        WebClient.RequestHeadersUriSpec deleteSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(client.delete()).thenReturn(deleteSpec);
        when(deleteSpec.uri(path, id)).thenReturn(headersSpec);
        when(headersSpec.headers(any(Consumer.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.error(error));
    }

    private WebClientResponseException notFoundException() {
        return WebClientResponseException.create(
                404, "Not Found", HttpHeaders.EMPTY, new byte[0], null);
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
