package com.marketplace.product_service.client.dto;

import lombok.Data;

@Data
public class StockInfo {
    private Integer productId;
    private Integer stock;
    private boolean hasStock;
}
