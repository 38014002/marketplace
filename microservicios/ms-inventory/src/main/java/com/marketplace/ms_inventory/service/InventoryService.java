package com.marketplace.ms_inventory.service;

import com.marketplace.ms_inventory.model.Inventory;
import com.marketplace.ms_inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<Inventory> listarTodo() {
        log.debug("Listando inventario completo");
        return inventoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Integer consultarStock(Integer productId) {
        log.debug("Consultando stock del producto {}", productId);
        return inventoryRepository.findByProductId(productId)
                .map(Inventory::getStock)
                .orElse(0);
    }

    @Transactional
    public Inventory crearInventario(Inventory inventory) {
        log.info("Creando inventario para producto {}", inventory.getProductId());
        boolean existe = inventoryRepository.findByProductId(inventory.getProductId()).isPresent();
        if (existe) {
            log.warn("Producto {} ya existe en inventario", inventory.getProductId());
            throw new RuntimeException(
                    "El producto con ID " + inventory.getProductId() + " ya está registrado en el inventario.");
        }
        return inventoryRepository.save(inventory);
    }

    @Transactional
    public Inventory actualizarStock(Integer productId, Integer cantidad) {
        log.info("Actualizando stock producto {} cantidad {}", productId, cantidad);

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElse(Inventory.builder()
                        .productId(productId)
                        .stock(0)
                        .build());

        int nuevoStock = inventory.getStock() + cantidad;

        if (nuevoStock < 0) {
            log.warn("Stock insuficiente para producto {}", productId);
            throw new RuntimeException("Operación cancelada: Stock insuficiente para el producto ID " + productId +
                    ". Stock actual: " + inventory.getStock());
        }

        inventory.setStock(nuevoStock);
        return inventoryRepository.save(inventory);
    }

    @Transactional
    public void eliminarInventarioPorProducto(Integer productId) {
        log.info("Eliminando inventario del producto {}", productId);
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró el producto ID " + productId + " en el inventario."));

        inventoryRepository.delete(inventory);
    }
}
