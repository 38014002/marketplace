package com.marketplace.ms_inventory.controller;

import com.marketplace.ms_inventory.dto.ApiResponse;
import com.marketplace.ms_inventory.dto.StockResponse;
import com.marketplace.ms_inventory.model.Inventory;
import com.marketplace.ms_inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Inventario", description = "Consulta y gestión de stock por producto")
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "Listar inventario completo")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Inventario consultado")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Inventory>>> obtenerInventarioGeneral() {
        List<Inventory> inventarioCompleto = inventoryService.listarTodo();

        ApiResponse<List<Inventory>> response = new ApiResponse<>(
                true,
                "Inventario general consultado exitosamente",
                inventarioCompleto,
                null);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Consultar stock de un producto")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Stock obtenido")
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<StockResponse>> obtenerStockPorProducto(
            @Parameter(description = "ID del producto en catálogo", example = "1") @PathVariable Integer productId) {
        Integer stock = inventoryService.consultarStock(productId);
        StockResponse stockResponse = new StockResponse(productId, stock, stock > 0);

        ApiResponse<StockResponse> response = new ApiResponse<>(
                true,
                "Stock del producto consultado exitosamente",
                stockResponse,
                null);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Registrar inventario", description = "Crea stock inicial para un producto")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Inventario creado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Producto ya registrado")
    @PostMapping
    public ResponseEntity<ApiResponse<Inventory>> crearInventario(@RequestBody Inventory inventory) {
        log.info("POST /api/v1/inventory - producto {}", inventory.getProductId());
        Inventory nuevoInventario = inventoryService.crearInventario(inventory);

        ApiResponse<Inventory> response = new ApiResponse<>(
                true,
                "Registro de inventario creado exitosamente",
                nuevoInventario,
                null);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar stock", description = "Incrementa o decrementa cantidad (delta)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Stock actualizado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Stock insuficiente")
    @PutMapping("/actualizar")
    public ResponseEntity<ApiResponse<Inventory>> actualizarStock(
            @Parameter(description = "ID del producto", example = "1") @RequestParam Integer productId,
            @Parameter(description = "Cantidad a sumar o restar", example = "5") @RequestParam Integer cantidad) {

        log.info("PUT /api/v1/inventory/actualizar - producto {} cantidad {}", productId, cantidad);
        Inventory inventoryActualizado = inventoryService.actualizarStock(productId, cantidad);

        ApiResponse<Inventory> response = new ApiResponse<>(
                true,
                "Stock actualizado exitosamente",
                inventoryActualizado,
                null);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar registro de inventario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Registro eliminado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado en inventario")
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> eliminarInventario(
            @Parameter(description = "ID del producto") @PathVariable Integer productId) {
        log.info("DELETE /api/v1/inventory/{}", productId);
        inventoryService.eliminarInventarioPorProducto(productId);

        ApiResponse<Void> response = new ApiResponse<>(
                true,
                "Registro de inventario eliminado correctamente",
                null,
                null);

        return ResponseEntity.ok(response);
    }
}
