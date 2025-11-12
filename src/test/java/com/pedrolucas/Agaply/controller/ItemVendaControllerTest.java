package com.pedrolucas.Agaply.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedrolucas.Agaply.dto.itemvenda.ItemVendaRequestDTO;
import com.pedrolucas.Agaply.dto.venda.VendaRequestDTO;
import com.pedrolucas.Agaply.model.*;
import com.pedrolucas.Agaply.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ItemVendaControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.5-alpine");

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private FornecedorRepository fornecedorRepository;
    @Autowired
    private EstoqueRepository estoqueRepository;
    @Autowired
    private VendaRepository vendaRepository;

    private Venda vendaSalva;

    @BeforeEach
    void setup() throws Exception {
        Categoria c = new Categoria();
        c.setNome("Bebidas");
        categoriaRepository.save(c);

        Fornecedor f = new Fornecedor();
        f.setNome("Ambev");
        f.setCnpj("11111111000199");
        f.setTelefone("11999998888");
        f.setEmail("ambev@teste.com");
        f.setAtivo(true);
        fornecedorRepository.save(f);

        Produto p = new Produto();
        p.setNome("Guaraná 2L");
        p.setPreco(BigDecimal.TEN);
        p.setQuantidadeMinima(10);
        p.setCategoria(c);
        p.setFornecedor(f);

        Estoque e = new Estoque();
        e.setProduto(p);
        e.setQuantidadeAtual(new BigDecimal("100"));
        e.setQuantidadeMinima(BigDecimal.TEN);
        p.setEstoque(e);
        Produto produtoSalvo = produtoRepository.save(p);

        ItemVendaRequestDTO itemDto = new ItemVendaRequestDTO(produtoSalvo.getId(), 10);
        VendaRequestDTO vendaDto = new VendaRequestDTO(List.of(itemDto), LocalDateTime.now());
        String requestJson = objectMapper.writeValueAsString(vendaDto);

        MvcResult result = mockMvc.perform(
                        post("/vendas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isCreated())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Integer idVenda = com.jayway.jsonpath.JsonPath.parse(jsonResponse).read("$.id");
        Integer idItem = com.jayway.jsonpath.JsonPath.parse(jsonResponse).read("$.itens[0].id");

        vendaSalva = vendaRepository.findById(idVenda.longValue()).orElseThrow();
    }


    @Test
    void deveRetornar404AoBuscarItemVendaInexistente() throws Exception {
        mockMvc.perform(
                        get("/itens-venda/{id}", 999L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar404AoBuscarItensDeVendaInexistente() throws Exception {
        mockMvc.perform(
                        get("/vendas/{vendaId}/itens", 999L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }
}