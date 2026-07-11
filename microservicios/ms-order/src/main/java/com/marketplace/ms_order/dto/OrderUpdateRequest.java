package com.marketplace.ms_order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.List;

@Data
public class OrderUpdateRequest {

    @NotNull(message = "userId es obligatorio")
    private Long userId;

    @NotBlank(message = "status es obligatorio")
    private String status;

    @NotNull(message = "totalAmount es obligatorio")
    @PositiveOrZero(message = "totalAmount no puede ser negativo")
    private Double totalAmount;

    private List<Long> productIds;
}
