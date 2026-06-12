package com.marketplace.ms_inventory.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Usamos Long para el ID de la tabla

    @Column(nullable = false, unique = true)
    private Integer productId; // ID del producto (Integer suele bastar aquí)

    @Column(nullable = false)
    private Integer stock;
}