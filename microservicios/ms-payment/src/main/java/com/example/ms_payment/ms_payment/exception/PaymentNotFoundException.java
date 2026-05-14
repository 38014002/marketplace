package com.example.ms_payment.ms_payment.exception;

public class PaymentNotFoundException
        extends RuntimeException {

    public PaymentNotFoundException(Long id) {
        super("Pago no encontrado con id: " + id);
    }
}