package com.marketplace.ms_inventory.service;

import com.marketplace.ms_inventory.model.Inventory;
import com.marketplace.ms_inventory.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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
}
