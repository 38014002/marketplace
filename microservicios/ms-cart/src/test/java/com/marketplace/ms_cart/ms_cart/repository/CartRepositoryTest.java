package com.marketplace.ms_cart.ms_cart.repository;

import com.marketplace.ms_cart.ms_cart.model.Cart;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Test
    void save_debePersistirItemCarrito() {
        // Given
        Cart cart = Cart.builder().userId(1L).productId(10L).quantity(2).build();

        // When
        Cart saved = cartRepository.save(cart);

        // Then
        assertNotNull(saved.getId());
        assertEquals(2, saved.getQuantity());
    }

    @Test
    void findByUserId_debeRetornarItemsDelUsuario() {
        // Given
        cartRepository.save(Cart.builder().userId(7L).productId(1L).quantity(1).build());
        cartRepository.save(Cart.builder().userId(7L).productId(2L).quantity(3).build());
        cartRepository.save(Cart.builder().userId(8L).productId(1L).quantity(1).build());

        // When
        var result = cartRepository.findByUserId(7L);

        // Then
        assertEquals(2, result.size());
    }
}
