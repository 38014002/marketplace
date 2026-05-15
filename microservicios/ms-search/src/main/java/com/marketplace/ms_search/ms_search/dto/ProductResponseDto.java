package com.marketplace.ms_search.ms_search.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
@Builder // <--- Esta es clave
@NoArgsConstructor
@AllArgsConstructor

public class ProductResponseDto {
    private Long id;
    private String name; // Coincide con Product.name
    private String description; // Coincide con Product.description
    private BigDecimal price; // Coincide con Product.price
    private String category; // Coincide con Product.category
}
