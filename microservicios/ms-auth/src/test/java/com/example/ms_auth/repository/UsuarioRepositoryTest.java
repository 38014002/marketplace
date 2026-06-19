package com.example.ms_auth.repository;

import com.example.ms_auth.model.Usuario;
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
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void save_debePersistirUsuario() {
        // Given
        Usuario usuario = new Usuario();
        usuario.setUsername("juan");
        usuario.setPassword("secret");
        usuario.setRole("ROLE_USER");

        // When
        Usuario saved = usuarioRepository.save(usuario);

        // Then
        assertNotNull(saved.getId());
        assertEquals("juan", saved.getUsername());
    }

    @Test
    void findByUsername_debeEncontrarUsuario() {
        // Given
        Usuario usuario = new Usuario();
        usuario.setUsername("maria");
        usuario.setPassword("pwd");
        usuario.setRole("ROLE_ADMIN");
        usuarioRepository.save(usuario);

        // When
        var result = usuarioRepository.findByUsername("maria");

        // Then
        assertTrue(result.isPresent());
        assertEquals("ROLE_ADMIN", result.get().getRole());
    }
}
