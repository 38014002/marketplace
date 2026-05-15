package com.marketplace.ms_order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Para que no envíe campos nulos al cliente
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data; // Aquí irá la orden o la lista de órdenes
    private Object error; // Aquí irán los mensajes de error (como el Map de validaciones)
}