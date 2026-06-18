package com.marketplace.product_service.client.dto;

import lombok.Data;

@Data
public class ServiceApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
}
