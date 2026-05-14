package com.marketplace.ms_search.ms_search.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductResponseDto {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private String categoria;
}