package com.example.ms_payment.ms_payment.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.example.ms_payment.ms_payment.dto.PaymentDTO;
import com.example.ms_payment.ms_payment.model.Payment;
import com.example.ms_payment.ms_payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.ms_payment.ms_payment.exception.PaymentNotFoundException;
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
	private final PaymentRepository repository;

	public java.util.List<Payment> listarTodos() {
	log.info("Listando todos los pagos");
	return repository.findAll();
	}

	public Payment buscarPorId(Long id) {
    log.info("Buscando pago con id {}", id);
    return repository.findById(id)
            .orElseThrow(() -> {
                log.warn("Pago no encontrado {}", id);
                return new PaymentNotFoundException(id);
            });
	}

	public Payment crear(PaymentDTO dto) {
    log.info("Procesando pago para orden {}",
            dto.getOrderId());
    if(dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
        log.error("Monto invalido");
        throw new IllegalArgumentException(
                "El monto debe ser mayor a 0");
    }
    Payment payment = new Payment();
    payment.setOrderId(dto.getOrderId());
    payment.setAmount(dto.getAmount());
    payment.setPaymentMethod(dto.getPaymentMethod());
    // simulacion de pago
    if(dto.getAmount().compareTo(new BigDecimal("100000")) > 0) {
        payment.setStatus("REJECTED");
    } else {
        payment.setStatus("APPROVED");
    }
    Payment saved = repository.save(payment);
    log.info("Pago guardado con id {}", saved.getId());
    return saved;
	}

	public Payment actualizar(Long id, PaymentDTO dto) {
    log.info("Actualizando pago {}", id);
    Payment payment = buscarPorId(id);
    payment.setPaymentMethod(dto.getPaymentMethod());
    payment.setAmount(dto.getAmount());
    Payment updated = repository.save(payment);
    log.info("Pago actualizado {}", updated.getId());
    return updated;
	}

	public void eliminar(Long id) {
    log.info("Eliminando pago {}", id);
    Payment payment = buscarPorId(id);
    repository.delete(payment);
    log.info("Pago eliminado {}", id);
	}
}