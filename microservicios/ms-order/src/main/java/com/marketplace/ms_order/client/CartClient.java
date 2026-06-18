package com.marketplace.ms_order.client;

import com.marketplace.ms_order.dto.CartItemDto;
import com.marketplace.ms_order.dto.ServiceApiResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Component
public class CartClient {

    private final WebClient webClient;

    public CartClient(@Qualifier("cartWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public List<CartItemDto> getCartByUser(Long userId, String token) {
        ServiceApiResponse<List<CartItemDto>> response = webClient
                .get()
                .uri("/user/{userId}", userId)
                .headers(headers -> applyAuth(headers, token))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ServiceApiResponse<List<CartItemDto>>>() {})
                .block();

        if (response == null || response.getData() == null) {
            return Collections.emptyList();
        }
        return response.getData();
    }

    private void applyAuth(HttpHeaders headers, String token) {
        if (token != null && !token.isBlank()) {
            headers.setBearerAuth(token);
        }
    }
}
