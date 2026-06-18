package com.marketplace.product_service.client.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CatalogProduct {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
}
