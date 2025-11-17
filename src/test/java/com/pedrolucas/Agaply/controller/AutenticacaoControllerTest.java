package com.pedrolucas.Agaply.controller;

import com.pedrolucas.Agaply.dto.autenticacao.AuthenticationDTO;
import com.pedrolucas.Agaply.model.Usuario;
import com.pedrolucas.Agaply.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AutenticacaoControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.5-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        String senhaCriptografada = passwordEncoder.encode("123456");
        Usuario usuario = new Usuario(null, "admin", senhaCriptografada, "ADMIN");
        usuarioRepository.save(usuario);
    }

    @Test
    void deveRealizarLoginComSucessoERetornarToken() throws Exception {
        AuthenticationDTO loginDTO = new AuthenticationDTO("admin", "123456");
        String jsonBody = objectMapper.writeValueAsString(loginDTO);

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void deveRetornar403AoTentarLogarComSenhaIncorreta() throws Exception {
        AuthenticationDTO loginDTO = new AuthenticationDTO("admin", "senha_errada");
        String jsonBody = objectMapper.writeValueAsString(loginDTO);

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deveRetornar403AoTentarLogarComUsuarioInexistente() throws Exception {
        AuthenticationDTO loginDTO = new AuthenticationDTO("usuario_fantasma", "123456");
        String jsonBody = objectMapper.writeValueAsString(loginDTO);

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                )
                .andExpect(status().is4xxClientError());
    }

}