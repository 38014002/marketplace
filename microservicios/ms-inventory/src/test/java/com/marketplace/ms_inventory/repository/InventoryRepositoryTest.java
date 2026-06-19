package com.marketplace.ms_inventory.repository;

import com.marketplace.ms_inventory.model.Inventory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class InventoryRepositoryTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Test
    void save_debePersistirInventario() {
        // Given
        Inventory inventory = Inventory.builder().productId(1).stock(10).build();

        // When
        Inventory saved = inventoryRepository.save(inventory);

        // Then
        assertNotNull(saved.getId());
        assertEquals(10, saved.getStock());
    }

    @Test
    void findByProductId_debeRetornarStock() {
        // Given
        inventoryRepository.save(Inventory.builder().productId(5).stock(20).build());

        // When
        var result = inventoryRepository.findByProductId(5);

        // Then
        assertTrue(result.isPresent());
        assertEquals(20, result.get().getStock());
    }
}
