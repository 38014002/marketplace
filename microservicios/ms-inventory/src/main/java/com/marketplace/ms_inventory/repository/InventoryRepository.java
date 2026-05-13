package com.marketplace.ms_inventory.repository;

import com.marketplace.ms_inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    /**
     * Busca el registro de inventario usando el ID del producto.
     * 
     * @param productId ID del producto que viene del microservicio de catálogo.
     * @return Un Optional con el objeto Inventory si existe.
     */
    Optional<Inventory> findByProductId(Integer productId);
}