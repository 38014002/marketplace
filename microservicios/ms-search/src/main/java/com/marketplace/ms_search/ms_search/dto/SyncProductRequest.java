package com.marketplace.ms_search.ms_search.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class SyncProductRequest {

    @NotNull(message = "id es obligatorio")
    private Long id;

    @NotBlank(message = "name es obligatorio")
    private String name;

    private String description;

    @NotNull(message = "price es obligatorio")
    @PositiveOrZero(message = "price no puede ser negativo")
    private Double price;

    private String category;
}
