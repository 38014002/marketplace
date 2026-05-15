package com.marketplace.ms_search.ms_search.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "search_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchProduct {
    @Id
    private Long id; // Este id viene del microservicio de Producto
    private String nombre;
    private String descripcion;
    private Double precio;
    private String categoria;
}