package com.marketplace.ms_order.client;

import com.marketplace.ms_order.dto.CartItemDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class CartClient {

    private final WebClient webClient;

    public CartClient(@Qualifier("cartWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public List<CartItemDto> getCartByUser(Long userId) {
        return webClient
                .get()
                .uri("/user/" + userId) // Se acopla a: http://localhost:8080/api/cart/user/{id}
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CartItemDto>>() {
                })
                .block();
    }
}