package com.marketplace.ms_inventory.repository;

import com.marketplace.ms_inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> { // o Integer dependiendo de tu @Id
    Optional<Inventory> findByProductId(Integer productId);
}