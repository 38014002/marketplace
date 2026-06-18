package com.marketplace.product_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDto {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    @NotBlank(message = "La descripción es obligatoria")
    private String description;
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal price;
    @NotBlank(message = "La categoría es obligatoria")
    private String category;
    private Integer stock;

    // Getters manuales (esto quita el error de raíz)
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public Integer getStock() {
        return stock;
    }
}