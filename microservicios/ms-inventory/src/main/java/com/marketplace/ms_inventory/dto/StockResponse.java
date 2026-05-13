package com.marketplace.ms_inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockResponse {
    private Integer productId;
    private Integer stock;
    private boolean hasStock;
}