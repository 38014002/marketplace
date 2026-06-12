package com.marketplace.ms_cart.ms_cart.exception;

public class CartNotFoundException
        extends RuntimeException {

    public CartNotFoundException(Long id) {
        super("Carrito no encontrado con id: " + id);
    }
}