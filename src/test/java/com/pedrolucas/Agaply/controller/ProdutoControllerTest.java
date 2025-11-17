package com.pedrolucas.Agaply.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedrolucas.Agaply.dto.produto.ProdutoRequestDTO;
import com.pedrolucas.Agaply.dto.produto.ProdutoUpdateDTO;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser
class ProdutoControllerTest {

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

    private Categoria categoriaSalva;
    private Fornecedor fornecedorSalvo;

    @BeforeEach
    void setup() {
        Categoria c = new Categoria();
        c.setNome("Hortifruti");
        categoriaSalva = categoriaRepository.save(c);

        Fornecedor f = new Fornecedor();
        f.setNome("Fazenda Sol");
        f.setCnpj("12345678000199");
        f.setTelefone("11999998888");
        f.setEmail("contato@fazendasol.com");
        f.setAtivo(true);
        fornecedorSalvo = fornecedorRepository.save(f);
    }

    private ProdutoRequestDTO criarProdutoRequestValido() {
        return new ProdutoRequestDTO("Maçã Gala", "789000111", new BigDecimal("7.99"), "Maçã Gala unidade", categoriaSalva.getId(), fornecedorSalvo.getId(), 10);
    }

    private Produto salvarProdutoValido(String nome, String codigoDeBarras) {
        Produto p = new Produto();
        p.setNome(nome);
        p.setCodigoDeBarras(codigoDeBarras);
        p.setPreco(new BigDecimal("9.99"));
        p.setDescricao("Desc Teste");
        p.setAtivo(true);
        p.setQuantidadeMinima(5);
        p.setCategoria(categoriaSalva);
        p.setFornecedor(fornecedorSalvo);

        Estoque e = new Estoque();
        e.setProduto(p);
        e.setQuantidadeAtual(BigDecimal.ZERO);
        e.setQuantidadeMinima(BigDecimal.valueOf(p.getQuantidadeMinima()));

        p.setEstoque(e);

        return produtoRepository.save(p);
    }


    @Test
    void deveCriarProdutoEEstoqueComSucesso() throws Exception {
        ProdutoRequestDTO requestDTO = criarProdutoRequestValido();
        String requestJson = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(
                        post("/produtos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Maçã Gala"));

        Produto produtoCriado = produtoRepository.findByNome("Maçã Gala").orElseThrow();

        Optional<Estoque> estoqueCriadoOpt = estoqueRepository.findByProdutoId(produtoCriado.getId());

        assertTrue(estoqueCriadoOpt.isPresent(), "O estoque não foi criado junto com o produto");
        assertEquals(0, BigDecimal.ZERO.compareTo(estoqueCriadoOpt.get().getQuantidadeAtual()));
        assertEquals(10, estoqueCriadoOpt.get().getQuantidadeMinima().intValue());
    }

    @Test
    void deveRetornar409AoCriarComNomeDuplicado() throws Exception {
        salvarProdutoValido("Maçã Gala", "123");

        ProdutoRequestDTO requestDTO = criarProdutoRequestValido();
        String requestJson = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(
                        post("/produtos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isConflict());
    }

    @Test
    void deveRetornar404AoCriarComFornecedorInexistente() throws Exception {
        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO("Maçã Gala", "789000111", new BigDecimal("7.99"), "Desc", categoriaSalva.getId(), 999L, 10);
        String requestJson = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(
                        post("/produtos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isNotFound());
    }


    @Test
    void deveBuscarProdutoPorIdComSucesso() throws Exception {
        Produto p = salvarProdutoValido("Maçã Gala", "123");
        Long idSalvo = p.getId();

        mockMvc.perform(
                        get("/produtos/{id}", idSalvo)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idSalvo))
                .andExpect(jsonPath("$.nome").value("Maçã Gala"));
    }

    @Test
    void deveRetornar404AoBuscarPorIdInexistente() throws Exception {
        mockMvc.perform(
                        get("/produtos/{id}", 999L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void deveBuscarTodosProdutosPaginado() throws Exception {
        salvarProdutoValido("Maçã Gala", "123");

        mockMvc.perform(
                        get("/produtos?page=0&size=5&sort=nome,asc")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].nome").value("Maçã Gala"));
    }

    @Test
    void deveAtualizarProdutoComSucesso() throws Exception {
        Produto p = salvarProdutoValido("Maçã Gala", "123");
        Long idSalvo = p.getId();

        ProdutoUpdateDTO updateDTO = new ProdutoUpdateDTO("Maçã Fuji", "789000222", new BigDecimal("8.50"), "Maçã Fuji unidade", categoriaSalva.getId(), fornecedorSalvo.getId(), 15, 0);
        String requestJson = objectMapper.writeValueAsString(updateDTO);

        mockMvc.perform(
                        put("/produtos/{id}", idSalvo)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maçã Fuji"))
                .andExpect(jsonPath("$.preco").value(8.50));
    }

    @Test
    void deveRetornar409AoAtualizarComNomeDuplicado() throws Exception {
        salvarProdutoValido("Maçã Gala", "123");
        Produto p2 = salvarProdutoValido("Maçã Fuji", "456");
        Long idFuji = p2.getId();

        ProdutoUpdateDTO updateDTO = new ProdutoUpdateDTO(
                "Maçã Gala", "456", new BigDecimal("8.50"), "Desc",
                categoriaSalva.getId(), fornecedorSalvo.getId(), 15, 0
        );
        String requestJson = objectMapper.writeValueAsString(updateDTO);

        mockMvc.perform(
                        put("/produtos/{id}", idFuji)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isConflict());
    }

    @Test
    void deveDesativarProdutoComSucesso() throws Exception {
        Produto p = salvarProdutoValido("Maçã Gala", "123");
        Long idSalvo = p.getId();

        mockMvc.perform(
                        delete("/produtos/{id}", idSalvo)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());

        Produto produtoInativo = produtoRepository.findById(idSalvo).orElseThrow();
        assertFalse(produtoInativo.isAtivo(), "O produto não foi marcado como inativo");
    }
}