package com.example.ms_payment.ms_payment.repository;

import com.example.ms_payment.ms_payment.model.Payment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrderId(Long orderId);
    List<Payment> findByStatus(String status);
}
