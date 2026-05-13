package com.marketplace.ms_inventory.service;

import com.marketplace.ms_inventory.model.Inventory;
import com.marketplace.ms_inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    /**
     * Consulta el stock disponible. Si el producto no existe, devuelve 0.
     */
    @Transactional(readOnly = true)
    public Integer consultarStock(Integer productId) {
        return inventoryRepository.findByProductId(productId)
                .map(Inventory::getStock)
                .orElse(0);
    }

    /**
     * Actualiza el stock (Suma o Resta).
     * Lanza excepción si el resultado es menor a 0.
     */
    @Transactional
    public Inventory actualizarStock(Integer productId, Integer cantidad) {
        // Buscamos el registro o creamos uno nuevo si no existe
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElse(Inventory.builder()
                        .productId(productId)
                        .stock(0)
                        .build());

        int nuevoStock = inventory.getStock() + cantidad;

        // Validación crítica para un Marketplace
        if (nuevoStock < 0) {
            throw new RuntimeException("Operación cancelada: Stock insuficiente para el producto ID " + productId +
                    ". Stock actual: " + inventory.getStock());
        }

        inventory.setStock(nuevoStock);
        return inventoryRepository.save(inventory);
    }
}