package com.marketplace.ms_order.client;

import com.marketplace.ms_order.dto.CartItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CartClient {

    private final WebClient webClient;

    public List<CartItemDto> getCartByUser(Long userId) {

        return webClient
                .get()
                .uri("http://localhost:8082/api/cart/user/" + userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CartItemDto>>() {})
                .block();
    }
}
