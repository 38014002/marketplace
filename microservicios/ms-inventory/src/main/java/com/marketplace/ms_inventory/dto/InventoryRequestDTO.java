package com.marketplace.ms_inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class InventoryRequestDTO {

    @NotNull(message = "productId es obligatorio")
    @Positive(message = "productId debe ser positivo")
    private Integer productId;

    @NotNull(message = "stock es obligatorio")
    @Min(value = 0, message = "stock no puede ser negativo")
    private Integer stock;
}
