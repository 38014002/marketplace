package com.marketplace.ms_cart.ms_cart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marketplace.ms_cart.ms_cart.model.Cart;

public interface CartRepository
        extends JpaRepository<Cart, Long> {

    List<Cart> findByUserId(Long userId);
}