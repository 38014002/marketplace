package com.marketplace.ms_cart.ms_cart.service;

import com.marketplace.ms_cart.ms_cart.dto.CartDto;
import com.marketplace.ms_cart.ms_cart.exception.CartNotFoundException;
import com.marketplace.ms_cart.ms_cart.model.Cart;
import com.marketplace.ms_cart.ms_cart.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock private CartRepository repository;
    @InjectMocks private CartService cartService;

    @Test
    void crear_debeGuardarItemConProductoYCantidad() {
        // Given
        CartDto dto = new CartDto(1L, 2L, 3);
        Cart saved = Cart.builder().id(10L).userId(1L).productId(2L).quantity(3).build();
        when(repository.save(any(Cart.class))).thenReturn(saved);

        // When
        Cart result = cartService.crear(dto);

        // Then
        assertEquals(10L, result.getId());
        assertEquals(2L, result.getProductId());
    }

    @Test
    void buscarPorId_cuandoNoExiste_debeLanzarExcepcion() {
        // Given
        when(repository.findById(5L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(CartNotFoundException.class, () -> cartService.buscarPorId(5L));
    }

    @Test
    void getCartByUser_debeRetornarItemsDelUsuario() {
        // Given
        when(repository.findByUserId(1L)).thenReturn(List.of(new Cart()));

        // When
        List<Cart> items = cartService.getCartByUser(1L);

        // Then
        assertEquals(1, items.size());
    }

    @Test
    void eliminar_debeBorrarItemExistente() {
        // Given
        Cart cart = Cart.builder().id(1L).build();
        when(repository.findById(1L)).thenReturn(Optional.of(cart));

        // When
        cartService.eliminar(1L);

        // Then
        verify(repository).delete(cart);
    }
}
