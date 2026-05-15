package com.marketplace.ms_inventory.controller;

import com.marketplace.ms_inventory.dto.StockResponse;
import com.marketplace.ms_inventory.model.Inventory;
import com.marketplace.ms_inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // 1. Ver stock de un producto
    @GetMapping("/{productId}")
    public ResponseEntity<StockResponse> obtenerStock(@PathVariable Integer productId) {
        Integer stock = inventoryService.consultarStock(productId);
        // Retorna el DTO con la lógica de si tiene stock disponible
        return ResponseEntity.ok(new StockResponse(productId, stock, stock > 0));
    }

    // 2. Agregar o actualizar stock (Ideal para el panel de administración o
    // procesos de compra)
    @PostMapping("/actualizar")
    public ResponseEntity<Inventory> actualizarStock(
            @RequestParam Integer productId,
            @RequestParam Integer cantidad) {

        return ResponseEntity.ok(inventoryService.actualizarStock(productId, cantidad));
    }
}