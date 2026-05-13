package com.marketplace.ms_catalog.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequestDTO {
    @NotBlank(message = "Nombre obligatorio")
    private String name;
    @NotBlank(message = "Descripción obligatoria")
    private String description;
    @NotNull(message = "Precio obligatorio")
    @DecimalMin("0.01")
    private BigDecimal price;
    @NotBlank(message = "Categoría obligatoria")
    private String category;
}