package com.pedrolucas.Agaply.service;

import com.pedrolucas.Agaply.dto.estoque.EstoqueRequestDTO;
import com.pedrolucas.Agaply.dto.estoque.EstoqueUpdateQuantidadeDTO;
import com.pedrolucas.Agaply.exception.ConflictException;
import com.pedrolucas.Agaply.mapper.EstoqueMapper;
import com.pedrolucas.Agaply.model.Estoque;
import com.pedrolucas.Agaply.model.Produto;
import com.pedrolucas.Agaply.repository.EstoqueRepository;
import com.pedrolucas.Agaply.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

    @Mock
    private EstoqueRepository estoqueRepository;
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private EstoqueMapper mapper;

    @InjectMocks
    private EstoqueService service;


    @Test
    void deveLancarConflictExceptionAoCriarEstoqueParaProdutoQueJaTem() {
        EstoqueRequestDTO dto = new EstoqueRequestDTO(1L, BigDecimal.TEN, BigDecimal.ONE);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(new Produto()));
        when(estoqueRepository.existsByProdutoId(1L)).thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> {
                    service.create(dto);
                }
        );

        verify(estoqueRepository, never()).save(any());
    }

    @Test
    void deveLancarIllegalArgumentExceptionAoCriarComQuantidadeNegativa() {
        EstoqueRequestDTO dto = new EstoqueRequestDTO(1L, new BigDecimal("-10"), BigDecimal.ONE);

        when(estoqueRepository.existsByProdutoId(1L)).thenReturn(false);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(new Produto()));

        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    service.create(dto);
                }
        );
    }


    @Test
    void deveLancarIllegalArgumentExceptionAoAtualizarComQuantidadeNegativa() {
        BigDecimal quantidadeNegativa = new BigDecimal("-50");
        EstoqueUpdateQuantidadeDTO dtoInvalido = new EstoqueUpdateQuantidadeDTO(quantidadeNegativa);

        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.of(new Estoque()));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    service.updateQuantidade(1L, dtoInvalido);
                }
        );

        assertEquals("A quantidade não pode ser negativa", exception.getMessage());
    }
}