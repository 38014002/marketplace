package com.example.ms_auth.service;

import java.util.Date;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.ms_auth.dto.AuthResponse;
import com.example.ms_auth.dto.LoginRequest;
import com.example.ms_auth.dto.RegisterRequest;
import com.example.ms_auth.model.RefreshToken;
import com.example.ms_auth.model.Usuario;
import com.example.ms_auth.repository.RefreshTokenRepository;
import com.example.ms_auth.repository.UsuarioRepository;
import com.example.ms_auth.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepo;
    private final RefreshTokenRepository refreshRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    // 🔹 REGISTER
    public AuthResponse register(RegisterRequest req) {

        Usuario user = new Usuario();
        user.setUsername(req.getUsername());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRole("ROLE_USER");

        usuarioRepo.save(user);

        String access = jwtUtil.generarToken(user.getUsername(), user.getRole());
        String refresh = generarRefreshToken(user.getUsername());

        return new AuthResponse(access, refresh);
    }

    // 🔹 LOGIN
    public AuthResponse login(LoginRequest req) {

        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        Usuario user = usuarioRepo.findByUsername(req.getUsername()).get();

        String access = jwtUtil.generarToken(user.getUsername(), user.getRole());
        String refresh = generarRefreshToken(user.getUsername());

        return new AuthResponse(access, refresh);
    }

    // 🔹 REFRESH
    public AuthResponse refresh(String refreshToken) {

        RefreshToken token = refreshRepo.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh inválido"));

        if (!jwtUtil.esValido(refreshToken) || !jwtUtil.esRefreshToken(refreshToken)) {
            throw new RuntimeException("Refresh token inválido");
        }

        Usuario user = usuarioRepo.findByUsername(token.getUsername()).get();

        String newAccess = jwtUtil.generarToken(user.getUsername(), user.getRole());

        return new AuthResponse(newAccess, refreshToken);
    }

    private String generarRefreshToken(String username) {

        String token = UUID.randomUUID().toString();

        RefreshToken rt = new RefreshToken();
        rt.setToken(token);
        rt.setUsername(username);
        rt.setExpiryDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24));

        refreshRepo.save(rt);

        return token;
    }
}
