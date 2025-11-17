package com.pedrolucas.Agaply.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedrolucas.Agaply.dto.fornecedor.FornecedorRequestDTO;
import com.pedrolucas.Agaply.dto.fornecedor.FornecedorUpdateDTO;
import com.pedrolucas.Agaply.model.Fornecedor;
import com.pedrolucas.Agaply.repository.FornecedorRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser
class FornecedorControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.5-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    private Fornecedor salvarFornecedorValido(String nome, String cnpj, boolean ativo) {
        Fornecedor f = new Fornecedor();
        f.setNome(nome);
        f.setCnpj(cnpj);
        f.setTelefone("11999998888");
        f.setEmail(nome.toLowerCase().replaceAll("\\s+", "") + "@teste.com");
        f.setAtivo(ativo);
        return fornecedorRepository.save(f);
    }

    @Test
    void deveCriarFornecedorComSucesso() throws Exception {
        FornecedorRequestDTO requestDTO = new FornecedorRequestDTO("Ambev", "07526557000100", "11999998888", "contato@ambev.com");
        String requestJson = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(
                        post("/fornecedores")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Ambev"))
                .andExpect(jsonPath("$.cnpj").value("07526557000100"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void deveRetornar409AoCriarFornecedorComCnpjDuplicado() throws Exception {
        salvarFornecedorValido("Ambev", "07526557000100", true);

        FornecedorRequestDTO requestDTO = new FornecedorRequestDTO("Coca-Cola", "07526557000100", "11777776666", "contato@coca.com");
        String requestJson = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(
                        post("/fornecedores")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isConflict());
    }

    @Test
    void deveRetornar400AoCriarComDadosInvalidos() throws Exception {
        FornecedorRequestDTO requestDTO = new FornecedorRequestDTO(null, null, null, null);
        String requestJson = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(
                        post("/fornecedores")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    void deveBuscarFornecedorPorIdComSucesso() throws Exception {
        Fornecedor f = salvarFornecedorValido("Ambev", "07526557000100", true);
        Long idSalvo = f.getId();

        mockMvc.perform(
                        get("/fornecedores/{id}", idSalvo)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idSalvo))
                .andExpect(jsonPath("$.nome").value("Ambev"));
    }

    @Test
    void deveRetornar404AoBuscarPorIdInexistente() throws Exception {
        mockMvc.perform(
                        get("/fornecedores/{id}", 999L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void deveBuscarTodosFornecedoresPaginado() throws Exception {
        salvarFornecedorValido("Ambev", "07526557000100", true);

        mockMvc.perform(
                        get("/fornecedores?page=0&size=5&sort=nome,asc")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].nome").value("Ambev"));
    }


    @Test
    void deveAtualizarFornecedorComSucesso() throws Exception {
        Fornecedor f = salvarFornecedorValido("Ambev", "07526557000100", true);
        Long idSalvo = f.getId();

        FornecedorUpdateDTO updateDTO = new FornecedorUpdateDTO("Ambev S.A.", "07526557000100", "11999998888", "contato@ambev.com");
        String requestJson = objectMapper.writeValueAsString(updateDTO);

        mockMvc.perform(
                        put("/fornecedores/{id}", idSalvo)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ambev S.A."));
    }

    @Test
    void deveRetornar409AoAtualizarComCnpjDuplicado() throws Exception {
        salvarFornecedorValido("Ambev", "07526557000100", true);
        Fornecedor f2 = salvarFornecedorValido("Coca-Cola", "00526557000199", true);
        Long idCoca = f2.getId();

        FornecedorUpdateDTO updateDTO = new FornecedorUpdateDTO("Coca-Cola Atualizado", "07526557000100", "11777776666", "contato@coca.com");
        String requestJson = objectMapper.writeValueAsString(updateDTO);

        mockMvc.perform(
                        put("/fornecedores/{id}", idCoca)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isConflict());
    }


    @Test
    void deveDesativarFornecedorComSucesso() throws Exception {
        Fornecedor f = salvarFornecedorValido("Ambev", "07526557000100", true);
        Long idSalvo = f.getId();

        mockMvc.perform(
                        delete("/fornecedores/{id}", idSalvo)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get("/fornecedores/{id}", idSalvo)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(false));
    }
}