package com.pedrolucas.Agaply.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedrolucas.Agaply.dto.estoque.EstoqueUpdateQuantidadeDTO;
import com.pedrolucas.Agaply.model.Categoria;
import com.pedrolucas.Agaply.model.Fornecedor;
import com.pedrolucas.Agaply.model.Estoque;
import com.pedrolucas.Agaply.model.Produto;
import com.pedrolucas.Agaply.repository.CategoriaRepository;
import com.pedrolucas.Agaply.repository.FornecedorRepository;
import com.pedrolucas.Agaply.repository.EstoqueRepository;
import com.pedrolucas.Agaply.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.math.BigDecimal;
import java.util.Optional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;


@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EstoqueControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

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

    private Produto produtoSalvo;
    
    @BeforeEach
    void setup() {
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
        e.setQuantidadeAtual(BigDecimal.ZERO);
        e.setQuantidadeMinima(BigDecimal.TEN);
        p.setEstoque(e);

        produtoSalvo = produtoRepository.save(p);
    }


    @Test
    void deveBuscarEstoquePorProdutoIdComSucesso() throws Exception {
        Long idProduto = produtoSalvo.getId();

        mockMvc.perform(
                        get("/estoques/produto/{produtoId}", idProduto)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.produtoId").value(idProduto))
                .andExpect(jsonPath("$.quantidadeAtual").value(0.00));
    }

    @Test
    void deveRetornar404AoBuscarEstoqueDeProdutoInexistente() throws Exception {
        mockMvc.perform(
                        get("/estoques/produto/{produtoId}", 999L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAtualizarQuantidadeDeEstoqueComSucesso() throws Exception {
        Long idProduto = produtoSalvo.getId();

        EstoqueUpdateQuantidadeDTO updateDTO = new EstoqueUpdateQuantidadeDTO(new BigDecimal("200.50"));
        String requestJson = objectMapper.writeValueAsString(updateDTO);

        mockMvc.perform(
                        put("/estoques/produto/{produtoId}", idProduto)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.produtoId").value(idProduto))
                .andExpect(jsonPath("$.quantidadeAtual").value(200.50));
    }

    @Test
    void deveRetornar400AoAtualizarEstoqueComQuantidadeNegativa() throws Exception {
        Long idProduto = produtoSalvo.getId();

        EstoqueUpdateQuantidadeDTO updateDTO = new EstoqueUpdateQuantidadeDTO(new BigDecimal("-50"));
        String requestJson = objectMapper.writeValueAsString(updateDTO);

        mockMvc.perform(
                        put("/estoques/produto/{produtoId}", idProduto)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar409AoTentarCriarEstoqueParaProdutoQueJaTem() throws Exception {
        String requestJson = String.format(
                """
                {
                    "produtoId": %d,
                    "quantidadeAtual": 10,
                    "quantidadeMinima": 1
                }
                """, produtoSalvo.getId()
        );

        mockMvc.perform(
                        post("/estoques")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isConflict());
    }


    @Test
    void deveDeletarEstoqueComSucesso() throws Exception {
        Long idProduto = produtoSalvo.getId();
        Long idEstoque = estoqueRepository.findByProdutoId(idProduto).orElseThrow().getId();

        mockMvc.perform(
                        delete("/estoques/produto/{produtoId}", idProduto)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());

        Optional<Estoque> estoqueDeletado = estoqueRepository.findById(idEstoque);
        assertFalse(estoqueDeletado.isPresent(), "O estoque não foi deletado do banco");
    }
}