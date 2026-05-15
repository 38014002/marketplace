package com.marketplace.ms_search.ms_search.dto;

import java.math.BigDecimal;

// Estos son los imports que te faltan y por eso te marca error en rojo
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {
    // Borra la línea: private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
}
