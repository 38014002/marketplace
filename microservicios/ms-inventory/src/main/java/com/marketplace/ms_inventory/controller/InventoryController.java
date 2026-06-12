package com.marketplace.ms_inventory.controller;

import com.marketplace.ms_inventory.dto.ApiResponse; // DTO estándar del proyecto
import com.marketplace.ms_inventory.dto.StockResponse;
import com.marketplace.ms_inventory.model.Inventory;
import com.marketplace.ms_inventory.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // ==========================================================
    // 1. GET - Consultar Inventario General (Todos los productos)
    // ==========================================================
    @GetMapping
    public ResponseEntity<ApiResponse<List<Inventory>>> obtenerInventarioGeneral() {
        // Llama al servicio para obtener la lista completa de registros
        List<Inventory> inventarioCompleto = inventoryService.listarTodo();

        ApiResponse<List<Inventory>> response = new ApiResponse<>(
                true,
                "Inventario general consultado exitosamente",
                inventarioCompleto,
                null);

        return ResponseEntity.ok(response);
    }

    // ==========================================================
    // 2. GET - Consultar Stock de un Producto Específico (Opcional/Detalle)
    // ==========================================================
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<StockResponse>> obtenerStockPorProducto(@PathVariable Integer productId) {
        Integer stock = inventoryService.consultarStock(productId);
        StockResponse stockResponse = new StockResponse(productId, stock, stock > 0);

        ApiResponse<StockResponse> response = new ApiResponse<>(
                true,
                "Stock del producto consultado exitosamente",
                stockResponse,
                null);

        return ResponseEntity.ok(response);
    }

    // ==========================================================
    // 3. POST - Crear/Registrar Nuevo Inventario
    // ==========================================================
    @PostMapping
    public ResponseEntity<ApiResponse<Inventory>> crearInventario(@RequestBody Inventory inventory) {
        Inventory nuevoInventario = inventoryService.crearInventario(inventory);

        ApiResponse<Inventory> response = new ApiResponse<>(
                true,
                "Registro de inventario creado exitosamente",
                nuevoInventario,
                null);

        return new ResponseEntity<>(response, HttpStatus.CREATED); // 201 Created
    }

    // ==========================================================
    // 4. PUT - Actualizar Stock Existente
    // ==========================================================
    @PutMapping("/actualizar")
    public ResponseEntity<ApiResponse<Inventory>> actualizarStock(
            @RequestParam Integer productId,
            @RequestParam Integer cantidad) {

        Inventory inventoryActualizado = inventoryService.actualizarStock(productId, cantidad);

        ApiResponse<Inventory> response = new ApiResponse<>(
                true,
                "Stock actualizado exitosamente",
                inventoryActualizado,
                null);

        return ResponseEntity.ok(response);
    }

    // ==========================================================
    // 5. DELETE - Eliminar Registro de Inventario
    // ==========================================================
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> eliminarInventario(@PathVariable Integer productId) {
        inventoryService.eliminarInventarioPorProducto(productId);

        ApiResponse<Void> response = new ApiResponse<>(
                true,
                "Registro de inventario eliminado correctamente",
                null,
                null);

        return ResponseEntity.ok(response);
    }
}