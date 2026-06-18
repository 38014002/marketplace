package com.marketplace.ms_user.service;

import com.marketplace.ms_user.dto.UserRegistrationDto;
import com.marketplace.ms_user.exception.RecursoNoEncontradoException;
import com.marketplace.ms_user.model.User;
import com.marketplace.ms_user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<User> listarTodos() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User buscarPorId(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario con ID " + id + " no encontrado"));
    }

    @Transactional
    public User registrar(UserRegistrationDto dto) {
        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .build();
        return userRepository.save(user);
    }

    // === NUEVO MÉTODO: ACTUALIZAR ===
    @Transactional
    public User actualizar(Integer id, UserRegistrationDto dto) {
        User userExistente = buscarPorId(id);

        userExistente.setUsername(dto.getUsername());
        userExistente.setEmail(dto.getEmail());
        userExistente.setRole(dto.getRole());

        // Solo re-encriptamos la contraseña si viene con información
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            userExistente.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return userRepository.save(userExistente);
    }

    @Transactional
    public void eliminar(Integer id) {
        User user = buscarPorId(id);
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> buscarPorUsernameOptional(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public User buscarPorUsername(String username) {
        return buscarPorUsernameOptional(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + username));
    }

}