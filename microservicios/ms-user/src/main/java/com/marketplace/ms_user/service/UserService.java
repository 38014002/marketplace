package com.marketplace.ms_user.service;

import com.marketplace.ms_user.dto.UserRegistrationDto;
import com.marketplace.ms_user.exception.RecursoNoEncontradoException;
import com.marketplace.ms_user.model.User;
import com.marketplace.ms_user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<User> listarTodos() {
        log.debug("Listando todos los usuarios");
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User buscarPorId(Integer id) {
        log.debug("Buscando usuario con ID {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado con ID {}", id);
                    return new RecursoNoEncontradoException("Usuario con ID " + id + " no encontrado");
                });
    }

    @Transactional
    public User registrar(UserRegistrationDto dto) {
        log.info("Registrando usuario {}", dto.getUsername());
        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .build();
        User saved = userRepository.save(user);
        log.info("Usuario registrado con ID {}", saved.getId());
        return saved;
    }

    @Transactional
    public User actualizar(Integer id, UserRegistrationDto dto) {
        log.info("Actualizando usuario {}", id);
        User userExistente = buscarPorId(id);

        userExistente.setUsername(dto.getUsername());
        userExistente.setEmail(dto.getEmail());
        userExistente.setRole(dto.getRole());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            userExistente.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return userRepository.save(userExistente);
    }

    @Transactional
    public void eliminar(Integer id) {
        log.info("Eliminando usuario {}", id);
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
