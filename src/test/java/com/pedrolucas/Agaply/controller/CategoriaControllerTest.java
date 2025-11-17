package com.pedrolucas.Agaply.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedrolucas.Agaply.dto.categoria.CategoriaRequestDTO;
import com.pedrolucas.Agaply.dto.categoria.CategoriaUpdateDTO;
import com.pedrolucas.Agaply.model.Categoria;
import com.pedrolucas.Agaply.repository.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser
class CategoriaControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.5-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoriaRepository categoriaRepository;


    @Test
    void deveCriarCategoriaComSucesso() throws Exception {
        CategoriaRequestDTO requestDTO = new CategoriaRequestDTO("Hortifruti", null);
        String requestJson = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(
                        post("/categorias")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Hortifruti"));
    }

    @Test
    void deveRetornarErro400AoTentarCriarComDadosInvalidos() throws Exception {
        CategoriaRequestDTO requestDTO = new CategoriaRequestDTO(null, null);
        String requestJson = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(
                post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        )
                .andExpect(status().isBadRequest());

    }

    @Test
    void deveRetornar200OkAoBuscarPorId() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setNome("Frutas");
        Categoria categoriaSalva = categoriaRepository.save(categoria);

        Long idSalvo = categoriaSalva.getId();

        mockMvc.perform(
                        get("/categorias/{id}", idSalvo)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idSalvo))
                .andExpect(jsonPath("$.nome").value("Frutas"));
    }

    @Test
    void deveRetornarErro404AoBuscarPorIdInexistente() throws Exception {
        mockMvc.perform(
                        get("/categorias/{id}", 999L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornarPaginaDeCategorias() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setNome("Padaria");
        categoria.setDescricao("Pães e Bolos");
        categoriaRepository.save(categoria);

        mockMvc.perform(
                        get("/categorias?page=0&size=5&sort=nome,asc")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].nome").value("Padaria"));
    }

    @Test
    void deveAtualizarCategoriaComSucesso() throws Exception {
        Categoria categoriaAntiga = new Categoria();
        categoriaAntiga.setNome("Bebidas Frias");
        Categoria categoriaSalva = categoriaRepository.save(categoriaAntiga);
        Long idSalvo = categoriaSalva.getId();

        CategoriaUpdateDTO updateDTO = new CategoriaUpdateDTO("Bebidas & Alcoólicos", "Nova descrição");
        String requestJson = objectMapper.writeValueAsString(updateDTO);

        mockMvc.perform(
                        put("/categorias/{id}", idSalvo)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idSalvo))
                .andExpect(jsonPath("$.nome").value("Bebidas & Alcoólicos"));
    }

    @Test
    void deveRetornar404AoTentarAtualizarCategoriaInexistente() throws Exception {
        CategoriaUpdateDTO updateDTO = new CategoriaUpdateDTO("Nome", "Desc");
        String requestJson = objectMapper.writeValueAsString(updateDTO);

        mockMvc.perform(
                        put("/categorias/{id}", 999L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar400AoTentarAtualizarComDadosInvalidos() throws Exception {
        Categoria c = new Categoria();
        c.setNome("Categoria Válida");
        Categoria categoriaSalva = categoriaRepository.save(c);
        Long idSalvo = categoriaSalva.getId();

        CategoriaUpdateDTO updateDTO = new CategoriaUpdateDTO(null, "Desc");
        String requestJson = objectMapper.writeValueAsString(updateDTO);

        mockMvc.perform(
                        put("/categorias/{id}", idSalvo)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveDeletarCategoriaComSucesso() throws Exception {
        Categoria c = new Categoria();
        c.setNome("Para Deletar");
        Categoria categoriaSalva = categoriaRepository.save(c);
        Long idSalvo = categoriaSalva.getId();

        mockMvc.perform(
                        delete("/categorias/{id}", idSalvo)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get("/categorias/{id}", idSalvo)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar404AoTentarDeletarCategoriaInexistente() throws Exception {
        mockMvc.perform(
                        delete("/categorias/{id}", 999L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

}