package com.pedrolucas.Agaply.service;

import com.pedrolucas.Agaply.dto.produto.ProdutoRequestDTO;
import com.pedrolucas.Agaply.exception.ConflictException;
import com.pedrolucas.Agaply.exception.FornecedorNotFoundException;
import com.pedrolucas.Agaply.mapper.ProdutoMapper;
import com.pedrolucas.Agaply.model.Categoria;
import com.pedrolucas.Agaply.model.Fornecedor;
import com.pedrolucas.Agaply.model.Estoque;
import com.pedrolucas.Agaply.model.Produto;
import com.pedrolucas.Agaply.repository.CategoriaRepository;
import com.pedrolucas.Agaply.repository.FornecedorRepository;
import com.pedrolucas.Agaply.repository.EstoqueRepository;
import com.pedrolucas.Agaply.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private FornecedorRepository fornecedorRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private EstoqueRepository estoqueRepository;
    @Mock
    private ProdutoMapper mapper;

    @InjectMocks
    private ProdutoService service;


    @Test
    void deveCriarProdutoEEstoqueComSucesso() {
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Maçã", "123", BigDecimal.TEN, "Desc", 1L, 1L, 10);

        Fornecedor fornecedor = new Fornecedor();
        Categoria categoria = new Categoria();

        Produto produtoEntidade = new Produto();
        produtoEntidade.setQuantidadeMinima(10);

        when(produtoRepository.existsByNome("Maçã")).thenReturn(false);
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(mapper.toEntity(dto)).thenReturn(produtoEntidade);
        when(produtoRepository.save(produtoEntidade)).thenReturn(produtoEntidade);

        service.create(dto);


        verify(produtoRepository).save(produtoEntidade);

        ArgumentCaptor<Estoque> estoqueCaptor = ArgumentCaptor.forClass(Estoque.class);

        verify(estoqueRepository).save(estoqueCaptor.capture());

        Estoque estoqueSalvo = estoqueCaptor.getValue();

        assertEquals(produtoEntidade, estoqueSalvo.getProduto());
        assertEquals(0, BigDecimal.ZERO.compareTo(estoqueSalvo.getQuantidadeAtual()));
        assertEquals(10, estoqueSalvo.getQuantidadeMinima().intValue());
    }

    @Test
    void deveLancarConflictExceptionAoCriarComNomeDuplicado() {
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Maçã", "123", BigDecimal.TEN, "Desc", 1L, 1L, 10);

        when(produtoRepository.existsByNome("Maçã")).thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> {
                    service.create(dto);
                }
        );

        verify(produtoRepository, never()).save(any());
        verify(estoqueRepository, never()).save(any());
    }

    @Test
    void deveLancarFornecedorNotFoundExceptionAoCriar() {
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Maçã", "123", BigDecimal.TEN, "Desc", 1L, 99L, 10);

        when(produtoRepository.existsByNome("Maçã")).thenReturn(false);
        when(fornecedorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                FornecedorNotFoundException.class,
                () -> {
                    service.create(dto);
                }
        );
    }


    @Test
    void deveDesativarProdutoComSucesso() {
        Produto entidadeExistente = new Produto();
        entidadeExistente.setId(1L);
        entidadeExistente.setAtivo(true);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(entidadeExistente));

        service.delete(1L);

        verify(produtoRepository).save(entidadeExistente);
        assertFalse(entidadeExistente.isAtivo());
    }
}