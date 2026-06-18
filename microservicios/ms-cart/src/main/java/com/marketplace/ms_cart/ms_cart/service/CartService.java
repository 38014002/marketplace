package com.marketplace.ms_cart.ms_cart.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.marketplace.ms_cart.ms_cart.repository.CartRepository;
import com.marketplace.ms_cart.ms_cart.dto.CartDto;
import com.marketplace.ms_cart.ms_cart.exception.CartNotFoundException;
import com.marketplace.ms_cart.ms_cart.model.Cart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository repository;

    // LISTAR TODOS
    public List<Cart> listarTodos() {
        log.info("Listando todos los carritos");
        return repository.findAll();
    }

    // BUSCAR POR ID
    public Cart buscarPorId(Long id) {
        log.info("Buscando carrito con id {}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Carrito no encontrado {}", id);
                    return new CartNotFoundException(id);
                });
    }

    // CREAR CARRO
    public Cart crear(CartDto dto) {

        log.info("Creando carrito para usuario {}",
                dto.getUserId());

        Cart cart = Cart.builder()
                .userId(dto.getUserId())
                .productId(dto.getProductId())
                .quantity(dto.getQuantity())
                .build();

        Cart saved = repository.save(cart);

        log.info("Carrito creado con id {}", saved.getId());

        return saved;
    }

    // ACTUALIZAR CARRO
    public Cart actualizar(Long id, CartDto dto) {

        log.info("Actualizando carrito {}", id);

        Cart cart = buscarPorId(id);

        cart.setUserId(dto.getUserId());

        Cart updated = repository.save(cart);

        log.info("Carrito actualizado {}", updated.getId());

        return updated;
    }

    // ELIMINAR CARRO
    public void eliminar(Long id) {

        log.info("Eliminando carrito {}", id);

        Cart cart = buscarPorId(id);

        repository.delete(cart);

        log.info("Carrito eliminado {}", id);
    }

    // BUSCAR CARROS POR USUARIO
    public List<Cart> buscarPorUsuario(Long userId) {

        log.info("Buscando carritos del usuario {}",
                userId);

        return repository.findByUserId(userId);
    }

    public List<Cart> getCartByUser(Long userId) {
        log.info("Obteniendo carrito para usuario {}", userId);
        return repository.findByUserId(userId);
    }
}