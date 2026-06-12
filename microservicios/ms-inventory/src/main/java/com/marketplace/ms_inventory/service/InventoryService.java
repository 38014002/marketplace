package com.marketplace.ms_inventory.service;

import com.marketplace.ms_inventory.model.Inventory;
import com.marketplace.ms_inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<Inventory> listarTodo() {
        return inventoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Integer consultarStock(Integer productId) {
        return inventoryRepository.findByProductId(productId)
                .map(Inventory::getStock)
                .orElse(0);
    }

    @Transactional
    public Inventory crearInventario(Inventory inventory) {
        boolean existe = inventoryRepository.findByProductId(inventory.getProductId()).isPresent();
        if (existe) {

            throw new RuntimeException(
                    "El producto con ID " + inventory.getProductId() + " ya está registrado en el inventario.");
        }
        return inventoryRepository.save(inventory);
    }

    @Transactional
    public Inventory actualizarStock(Integer productId, Integer cantidad) {

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElse(Inventory.builder()
                        .productId(productId)
                        .stock(0)
                        .build());

        int nuevoStock = inventory.getStock() + cantidad;

        if (nuevoStock < 0) {
            throw new RuntimeException("Operación cancelada: Stock insuficiente para el producto ID " + productId +
                    ". Stock actual: " + inventory.getStock());
        }

        inventory.setStock(nuevoStock);
        return inventoryRepository.save(inventory);
    }

    @Transactional
    public void eliminarInventarioPorProducto(Integer productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró el producto ID " + productId + " en el inventario."));

        inventoryRepository.delete(inventory);
    }
}