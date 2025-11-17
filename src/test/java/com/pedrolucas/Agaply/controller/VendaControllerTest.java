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
import org.springframework.security.test.context.support.WithMockUser;
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
@WithMockUser
class VendaControllerTest {

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

    private Categoria categoriaSalva;
    private Fornecedor fornecedorSalvo;
    private Produto produtoSalvo;
    private final BigDecimal PRECO_PRODUTO = new BigDecimal("10.00");
    private final BigDecimal ESTOQUE_INICIAL = new BigDecimal("100.00");

    @BeforeEach
    void setup() {
        Categoria c = new Categoria();
        c.setNome("Bebidas");
        c.setDescricao("Refrigerantes e sucos");
        categoriaSalva = categoriaRepository.save(c);

        Fornecedor f = new Fornecedor();
        f.setNome("Ambev");
        f.setCnpj("11111111000199");
        f.setTelefone("11999998888");
        f.setEmail("ambev@teste.com");
        f.setAtivo(true);
        fornecedorSalvo = fornecedorRepository.save(f);

        Produto p = new Produto();
        p.setNome("Guaraná 2L");
        p.setCodigoDeBarras("123456");
        p.setPreco(PRECO_PRODUTO);
        p.setDescricao("Refrigerante");
        p.setAtivo(true);
        p.setQuantidadeMinima(10);
        p.setCategoria(categoriaSalva);
        p.setFornecedor(fornecedorSalvo);

        Estoque e = new Estoque();
        e.setProduto(p);
        e.setQuantidadeAtual(ESTOQUE_INICIAL);
        e.setQuantidadeMinima(BigDecimal.TEN);
        p.setEstoque(e);

        produtoSalvo = produtoRepository.save(p);
    }

    private VendaRequestDTO criarVendaRequest(Long produtoId, int quantidade) {
        ItemVendaRequestDTO item = new ItemVendaRequestDTO(produtoId, quantidade);
        return new VendaRequestDTO(List.of(item), LocalDateTime.now());
    }


    @Test
    void deveCriarVendaEBaixarEstoqueComSucesso() throws Exception {
        VendaRequestDTO requestDTO = criarVendaRequest(produtoSalvo.getId(), 5);
        String requestJson = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(
                        post("/vendas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.valorTotal").value(50))
                .andExpect(jsonPath("$.itens[0].produtoId").value(produtoSalvo.getId()))
                .andExpect(jsonPath("$.itens[0].nomeProduto").value("Guaraná 2L"));

        Estoque estoqueAtualizado = estoqueRepository.findByProdutoId(produtoSalvo.getId()).orElseThrow();
        assertEquals(0, new BigDecimal("95.00").compareTo(estoqueAtualizado.getQuantidadeAtual()));
    }

    @Test
    void deveRetornar409AoTentarComprarComEstoqueInsuficiente() throws Exception {
        VendaRequestDTO requestDTO = criarVendaRequest(produtoSalvo.getId(), 200);
        String requestJson = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(
                        post("/vendas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isConflict());

        Estoque estoqueAtualizado = estoqueRepository.findByProdutoId(produtoSalvo.getId()).orElseThrow();
        assertEquals(0, ESTOQUE_INICIAL.compareTo(estoqueAtualizado.getQuantidadeAtual()));
    }


    @Test
    void deveBuscarVendaPorId() throws Exception {
        Venda venda = new Venda();
        venda.setDataHora(LocalDateTime.now());
        venda.setValorTotal(BigDecimal.TEN);
        Venda vendaSalva = vendaRepository.save(venda);
        Long idSalvo = vendaSalva.getId();

        mockMvc.perform(
                        get("/vendas/{id}", idSalvo)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idSalvo));
    }


    @Test
    void deveCancelarVendaEDevolverEstoqueComSucesso() throws Exception {
        VendaRequestDTO requestDTO = criarVendaRequest(produtoSalvo.getId(), 10);
        String requestJson = objectMapper.writeValueAsString(requestDTO);

        MvcResult result = mockMvc.perform(
                post("/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        ).andReturn();


        String jsonResponse = result.getResponse().getContentAsString();
        Integer idVendaCriada = com.jayway.jsonpath.JsonPath.parse(jsonResponse).read("$.id");
        Long idVenda = idVendaCriada.longValue();

        mockMvc.perform(
                        patch("/vendas/{id}", idVenda)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cancelada").value(true));

        Estoque estoqueAtualizado = estoqueRepository.findByProdutoId(produtoSalvo.getId()).orElseThrow();
        assertEquals(0, ESTOQUE_INICIAL.compareTo(estoqueAtualizado.getQuantidadeAtual()));
    }

    @Test
    void deveRetornar404AoCancelarVendaInexistente() throws Exception {
        mockMvc.perform(
                        patch("/vendas/{id}", 999L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar409AoCancelarVendaJaCancelada() throws Exception {
        Venda venda = new Venda();
        venda.setDataHora(LocalDateTime.now());
        venda.setValorTotal(BigDecimal.TEN);
        venda.setCancelada(true);
        Venda vendaSalva = vendaRepository.save(venda);
        Long idSalvo = vendaSalva.getId();

        mockMvc.perform(
                        patch("/vendas/{id}", idSalvo)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isConflict());
    }
}