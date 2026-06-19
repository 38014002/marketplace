package com.marketplace.ms_user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.ms_user.dto.UserRegistrationDto;
import com.marketplace.ms_user.model.User;
import com.marketplace.ms_user.security.JwtUtil;
import com.marketplace.ms_user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void listar_debeRetornar200() throws Exception {
        // Given
        when(userService.listarTodos()).thenReturn(List.of(new User()));

        // When / Then
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void guardar_debeRetornar201() throws Exception {
        // Given
        UserRegistrationDto dto = UserRegistrationDto.builder()
                .username("ana")
                .email("ana@test.com")
                .password("clave")
                .role("USER")
                .build();
        when(userService.registrar(any())).thenReturn(User.builder().id(1).username("ana").build());

        // When / Then
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.username").value("ana"));
    }

    @Test
    void login_conCredencialesValidas_debeRetornarToken() throws Exception {
        // Given
        User user = User.builder().username("juan").password("hash").role("USER").build();
        when(userService.buscarPorUsernameOptional("juan")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("clave", "hash")).thenReturn(true);
        when(jwtService.generarToken("juan", "USER")).thenReturn("token-jwt");

        // When / Then
        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"juan\",\"password\":\"clave\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("token-jwt"));
    }
}
