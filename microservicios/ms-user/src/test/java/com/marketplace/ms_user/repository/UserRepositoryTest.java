package com.marketplace.ms_user.repository;

import com.marketplace.ms_user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.flyway.enabled=false"
})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_debePersistirUsuario() {
        // Given
        User user = User.builder()
                .username("testuser")
                .password("encoded")
                .email("test@test.com")
                .role("USER")
                .build();

        // When
        User saved = userRepository.save(user);

        // Then
        assertNotNull(saved.getId());
        assertEquals("testuser", saved.getUsername());
    }

    @Test
    void findByUsername_debeRetornarUsuario() {
        // Given
        userRepository.save(User.builder()
                .username("ana")
                .password("x")
                .email("ana@test.com")
                .role("ADMIN")
                .build());

        // When
        var result = userRepository.findByUsername("ana");

        // Then
        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getRole());
    }
}
