package com.marketplace.ms_user.service;

import com.marketplace.ms_user.dto.UserRegistrationDto;
import com.marketplace.ms_user.exception.RecursoNoEncontradoException;
import com.marketplace.ms_user.model.User;
import com.marketplace.ms_user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserService userService;

    @Test
    void listarTodos_debeRetornarUsuarios() {
        // Given
        when(userRepository.findAll()).thenReturn(List.of(new User()));

        // When
        List<User> result = userService.listarTodos();

        // Then
        assertEquals(1, result.size());
    }

    @Test
    void buscarPorId_cuandoNoExiste_debeLanzarExcepcion() {
        // Given
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(RecursoNoEncontradoException.class, () -> userService.buscarPorId(99));
    }

    @Test
    void registrar_debeEncriptarPasswordYGuardar() {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("ana");
        dto.setEmail("ana@test.com");
        dto.setPassword("clave");
        dto.setRole("ADMIN");

        when(passwordEncoder.encode("clave")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        User saved = userService.registrar(dto);

        // Then
        assertEquals("ana", saved.getUsername());
        verify(passwordEncoder).encode("clave");
    }

    @Test
    void eliminar_debeBorrarUsuarioExistente() {
        // Given
        User user = User.builder().id(1).username("x").build();
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // When
        userService.eliminar(1);

        // Then
        verify(userRepository).delete(user);
    }
}
