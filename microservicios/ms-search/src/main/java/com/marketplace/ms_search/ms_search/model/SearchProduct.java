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
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String category;
}