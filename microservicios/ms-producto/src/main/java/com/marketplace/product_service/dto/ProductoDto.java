package com.marketplace.product_service.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDto {
    private String name;
    private String description;
    private BigDecimal price;
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