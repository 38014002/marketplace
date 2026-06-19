package com.example.ms_payment.ms_payment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.ms_payment.ms_payment.model.Payment;
import com.example.ms_payment.ms_payment.service.PaymentService;
import com.example.ms_payment.ms_payment.dto.PaymentDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Pagos", description = "Gestión de pagos y procesamiento de órdenes")
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService service;

    @Operation(summary = "Listar todos los pagos")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    @GetMapping
    public ResponseEntity<List<Payment>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Obtener pago por ID")
    @ApiResponse(responseCode = "200", description = "Pago encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<Payment> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Crear pago", description = "Registra un pago simulado según monto y método")
    @ApiResponse(responseCode = "201", description = "Pago creado")
    @PostMapping
    public ResponseEntity<Payment> crear(@Valid @RequestBody PaymentDTO dto) {
        log.info("POST /api/pagos - orden {}", dto.getOrderId());
        Payment creado = service.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Actualizar pago")
    @ApiResponse(responseCode = "200", description = "Pago actualizado")
    @PutMapping("/{id}")
    public ResponseEntity<Payment> actualizar(@PathVariable Long id, @Valid @RequestBody PaymentDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar pago")
    @ApiResponse(responseCode = "204", description = "Pago eliminado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Procesar pago de orden", description = "Endpoint usado por ms-order en checkout")
    @ApiResponse(responseCode = "200", description = "Resultado APPROVED o REJECTED")
    @PostMapping("/process/{orderId}")
    public ResponseEntity<String> processPayment(@PathVariable Long orderId) {
        log.info("POST /api/pagos/process/{}", orderId);
        return ResponseEntity.ok("APPROVED");
    }
}
