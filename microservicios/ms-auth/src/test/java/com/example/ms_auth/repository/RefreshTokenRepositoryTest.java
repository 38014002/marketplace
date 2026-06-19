package com.example.ms_auth.repository;

import com.example.ms_auth.model.RefreshToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void save_debePersistirToken() {
        // Given
        RefreshToken token = new RefreshToken();
        token.setToken("abc-123");
        token.setUsername("user1");
        token.setExpiryDate(new Date());

        // When
        RefreshToken saved = refreshTokenRepository.save(token);

        // Then
        assertNotNull(saved.getId());
        assertEquals("abc-123", saved.getToken());
    }

    @Test
    void findByToken_debeEncontrarToken() {
        // Given
        RefreshToken token = new RefreshToken();
        token.setToken("refresh-xyz");
        token.setUsername("user2");
        token.setExpiryDate(new Date());
        refreshTokenRepository.save(token);

        // When
        var result = refreshTokenRepository.findByToken("refresh-xyz");

        // Then
        assertTrue(result.isPresent());
        assertEquals("user2", result.get().getUsername());
    }
}
