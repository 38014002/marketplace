package com.marketplace.ms_review.ms_review.exception;

public class ReviewNotFoundException
        extends RuntimeException {

    public ReviewNotFoundException(Long id) {
        super("Pago no encontrado con id: " + id);
    }
}