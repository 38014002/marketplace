package com.marketplace.ms_inventory.exception;

import com.marketplace.ms_inventory.dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(
                ApiResponse.<Object>builder()
                        .success(false)
                        .message("Error de validación en Inventario")
                        .error(errores)
                        .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraint(ConstraintViolationException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getConstraintViolations().forEach(v ->
                errores.put(v.getPropertyPath().toString(), v.getMessage()));

        return ResponseEntity.badRequest().body(
                ApiResponse.<Object>builder()
                        .success(false)
                        .message("Parámetros inválidos en Inventario")
                        .error(errores)
                        .build());
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(404).body(
                ApiResponse.<Object>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneral(Exception ex) {
        return ResponseEntity.status(500).body(
                ApiResponse.<Object>builder()
                        .success(false)
                        .message("Error crítico en el servicio de inventario: " + ex.getMessage())
                        .build());
    }
}