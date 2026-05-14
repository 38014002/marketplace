package com.example.ms_payment.ms_payment.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentDTO {
    @NotNull
    private Long orderId;

    @Positive
    private BigDecimal amount;

    @NotBlank
    private String paymentMethod;
}
