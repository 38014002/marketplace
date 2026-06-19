package com.marketplace.ms_inventory.service;

import com.marketplace.ms_inventory.model.Inventory;
import com.marketplace.ms_inventory.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock private InventoryRepository inventoryRepository;
    @InjectMocks private InventoryService inventoryService;

    @Test
    void consultarStock_cuandoNoExiste_debeRetornarCero() {
        // Given
        when(inventoryRepository.findByProductId(1)).thenReturn(Optional.empty());

        // When
        Integer stock = inventoryService.consultarStock(1);

        // Then
        assertEquals(0, stock);
    }

    @Test
    void crearInventario_cuandoYaExiste_debeLanzarExcepcion() {
        // Given
        Inventory inv = Inventory.builder().productId(1).stock(5).build();
        when(inventoryRepository.findByProductId(1)).thenReturn(Optional.of(inv));

        // When / Then
        assertThrows(RuntimeException.class, () -> inventoryService.crearInventario(inv));
    }

    @Test
    void actualizarStock_debeIncrementarCantidad() {
        // Given
        Inventory inv = Inventory.builder().productId(1).stock(10).build();
        when(inventoryRepository.findByProductId(1)).thenReturn(Optional.of(inv));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));

        // When
        Inventory updated = inventoryService.actualizarStock(1, 5);

        // Then
        assertEquals(15, updated.getStock());
    }

    @Test
    void actualizarStock_conStockInsuficiente_debeLanzarExcepcion() {
        // Given
        Inventory inv = Inventory.builder().productId(1).stock(2).build();
        when(inventoryRepository.findByProductId(1)).thenReturn(Optional.of(inv));

        // When / Then
        assertThrows(RuntimeException.class, () -> inventoryService.actualizarStock(1, -10));
    }

    @Test
    void listarTodo_debeRetornarInventario() {
        when(inventoryRepository.findAll()).thenReturn(List.of(new Inventory()));

        assertEquals(1, inventoryService.listarTodo().size());
    }

    @Test
    void consultarStock_cuandoExiste_debeRetornarCantidad() {
        Inventory inv = Inventory.builder().productId(1).stock(7).build();
        when(inventoryRepository.findByProductId(1)).thenReturn(Optional.of(inv));

        assertEquals(7, inventoryService.consultarStock(1));
    }

    @Test
    void crearInventario_debeGuardarNuevoRegistro() {
        Inventory inv = Inventory.builder().productId(2).stock(5).build();
        when(inventoryRepository.findByProductId(2)).thenReturn(Optional.empty());
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));

        Inventory saved = inventoryService.crearInventario(inv);

        assertEquals(5, saved.getStock());
    }

    @Test
    void eliminarInventarioPorProducto_debeEliminarRegistro() {
        Inventory inv = Inventory.builder().productId(3).stock(1).build();
        when(inventoryRepository.findByProductId(3)).thenReturn(Optional.of(inv));

        inventoryService.eliminarInventarioPorProducto(3);

        verify(inventoryRepository).delete(inv);
    }

    @Test
    void actualizarStock_productoNuevo_debeCrearConStock() {
        when(inventoryRepository.findByProductId(5)).thenReturn(Optional.empty());
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));

        Inventory result = inventoryService.actualizarStock(5, 10);

        assertEquals(10, result.getStock());
    }
}
