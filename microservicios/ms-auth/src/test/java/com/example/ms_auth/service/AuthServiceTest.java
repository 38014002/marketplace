package com.example.ms_auth.service;

import com.example.ms_auth.dto.AuthResponse;
import com.example.ms_auth.dto.LoginRequest;
import com.example.ms_auth.dto.RegisterRequest;
import com.example.ms_auth.model.RefreshToken;
import com.example.ms_auth.model.Usuario;
import com.example.ms_auth.repository.RefreshTokenRepository;
import com.example.ms_auth.repository.UsuarioRepository;
import com.example.ms_auth.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UsuarioRepository usuarioRepo;
    @Mock private RefreshTokenRepository refreshRepo;
    @Mock private PasswordEncoder encoder;
    @Mock private AuthenticationManager authManager;
    @Mock private JwtUtil jwtUtil;
    @InjectMocks private AuthService authService;

    @Test
    void register_debeCrearUsuarioYRetornarTokens() {
        // Given
        RegisterRequest req = new RegisterRequest();
        req.setUsername("nuevo");
        req.setPassword("secret");

        when(encoder.encode("secret")).thenReturn("hash");
        when(usuarioRepo.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtUtil.generarToken("nuevo", "ROLE_USER")).thenReturn("access-token");

        // When
        AuthResponse response = authService.register(req);

        // Then
        assertEquals("access-token", response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        verify(usuarioRepo).save(any(Usuario.class));
        verify(refreshRepo).save(any(RefreshToken.class));
    }

    @Test
    void login_debeAutenticarYRetornarTokens() {
        // Given
        LoginRequest req = new LoginRequest();
        req.setUsername("juan");
        req.setPassword("1234");

        Usuario user = new Usuario();
        user.setUsername("juan");
        user.setRole("ROLE_USER");

        when(usuarioRepo.findByUsername("juan")).thenReturn(Optional.of(user));
        when(jwtUtil.generarToken("juan", "ROLE_USER")).thenReturn("jwt-access");

        // When
        AuthResponse response = authService.login(req);

        // Then
        assertEquals("jwt-access", response.getAccessToken());
        verify(authManager).authenticate(any());
    }

    @Test
    void refresh_conTokenInvalido_debeLanzarExcepcion() {
        // Given
        when(refreshRepo.findByToken("bad")).thenReturn(Optional.empty());

        // When / Then
        assertThrows(RuntimeException.class, () -> authService.refresh("bad"));
    }
}
