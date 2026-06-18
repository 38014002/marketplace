package com.marketplace.ms_user.exception;

import com.marketplace.ms_user.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 🔴 VALIDACIÓN (Cuando los datos enviados no son correctos)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(
                ApiResponse.<Object>builder()
                        .success(false)
                        .message("Validación fallida en Usuario")
                        .error(errores)
                        .build());
    }

    // 🔎 404 (Cuando el recurso no existe)
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(404).body(
                ApiResponse.<Object>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .build());
    }

    // 🚫 403 (Sin permisos para el recurso)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(403).body(
                ApiResponse.<Object>builder()
                        .success(false)
                        .message("Acceso denegado: se requiere rol ADMIN para esta operación")
                        .build());
    }

    // 💥 500 (Errores genéricos del servidor)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneral(Exception ex) {
        return ResponseEntity.status(500).body(
                ApiResponse.<Object>builder()
                        .success(false)
                        .message("Error interno en el servicio de usuarios: " + ex.getMessage())
                        .build());
    }
}