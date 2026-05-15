package com.marketplace.ms_cart.ms_cart.controller;

import com.marketplace.ms_cart.ms_cart.model.Cart;
import com.marketplace.ms_cart.ms_cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Cart>> getCartByUser(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                cartService.getCartByUser(userId)
        );
    }
}