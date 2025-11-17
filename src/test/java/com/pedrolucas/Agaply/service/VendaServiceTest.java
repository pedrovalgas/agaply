package com.pedrolucas.Agaply.service;

import com.pedrolucas.Agaply.dto.itemvenda.ItemVendaRequestDTO;
import com.pedrolucas.Agaply.dto.venda.VendaRequestDTO;
import com.pedrolucas.Agaply.exception.EstoqueInsuficienteException;
import com.pedrolucas.Agaply.exception.VendaJaCanceladaException;
import com.pedrolucas.Agaply.mapper.VendaMapper;
import com.pedrolucas.Agaply.model.Estoque;
import com.pedrolucas.Agaply.model.ItemVenda;
import com.pedrolucas.Agaply.model.Produto;
import com.pedrolucas.Agaply.model.Venda;
import com.pedrolucas.Agaply.repository.EstoqueRepository;
import com.pedrolucas.Agaply.repository.ProdutoRepository;
import com.pedrolucas.Agaply.repository.VendaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private VendaMapper mapper;

    @InjectMocks
    private VendaService service;

    @Test
    void deveCriarVendaEBaixarEstoqueComSucesso() {

        ItemVendaRequestDTO itemDto = new ItemVendaRequestDTO(1L, 2);
        VendaRequestDTO requestDto = new VendaRequestDTO(List.of(itemDto), LocalDateTime.now());

        Produto produtoMock = new Produto();
        produtoMock.setId(1L);
        produtoMock.setPreco(new BigDecimal("10.00"));

        Estoque estoqueMock = new Estoque();
        estoqueMock.setId(100L);
        estoqueMock.setProduto(produtoMock);
        estoqueMock.setQuantidadeAtual(new BigDecimal("50"));

        Venda vendaEntidade = new Venda();
        ItemVenda itemEntidade = new ItemVenda();
        itemEntidade.setProduto(produtoMock);
        itemEntidade.setQuantidade(new BigDecimal("2"));
        vendaEntidade.setItensVenda(List.of(itemEntidade));

        when(mapper.toEntity(requestDto)).thenReturn(vendaEntidade);

        when(produtoRepository.findAllById(anySet())).thenReturn(List.of(produtoMock));

        when(estoqueRepository.findAllByProdutoIdIn(anySet())).thenReturn(List.of(estoqueMock));

        when(vendaRepository.save(any(Venda.class))).thenReturn(vendaEntidade);

        service.create(requestDto);


        verify(vendaRepository, times(2)).save(any(Venda.class));

        assertEquals(0, new BigDecimal("48").compareTo(estoqueMock.getQuantidadeAtual()));

        assertEquals(0, new BigDecimal("10.00").compareTo(itemEntidade.getPrecoUnitario()));
        assertEquals(0, new BigDecimal("20.00").compareTo(itemEntidade.getSubtotal()));
        assertEquals(0, new BigDecimal("20.00").compareTo(vendaEntidade.getValorTotal()));
    }

    @Test
    void deveLancarEstoqueInsuficienteException() {
        ItemVendaRequestDTO itemDto = new ItemVendaRequestDTO(1L, 100);
        VendaRequestDTO requestDto = new VendaRequestDTO(List.of(itemDto), LocalDateTime.now());

        Produto produtoMock = new Produto();
        produtoMock.setId(1L);

        Estoque estoqueMock = new Estoque();
        estoqueMock.setProduto(produtoMock);
        estoqueMock.setQuantidadeAtual(new BigDecimal("50"));

        Venda vendaEntidade = new Venda();
        ItemVenda itemEntidade = new ItemVenda();
        itemEntidade.setProduto(produtoMock);
        itemEntidade.setQuantidade(new BigDecimal("100"));
        vendaEntidade.setItensVenda(List.of(itemEntidade));

        when(mapper.toEntity(requestDto)).thenReturn(vendaEntidade);
        when(produtoRepository.findAllById(anySet())).thenReturn(List.of(produtoMock));
        when(estoqueRepository.findAllByProdutoIdIn(anySet())).thenReturn(List.of(estoqueMock));
        when(vendaRepository.save(any(Venda.class))).thenReturn(vendaEntidade);

        assertThrows(
                EstoqueInsuficienteException.class,
                () -> {
                    service.create(requestDto);
                }
        );

        assertEquals(0, new BigDecimal("50").compareTo(estoqueMock.getQuantidadeAtual()));
    }


    @Test
    void deveCancelarVendaEDevolverEstoque() {
        Produto produtoMock = new Produto();
        produtoMock.setId(1L);

        Estoque estoqueMock = new Estoque();
        estoqueMock.setProduto(produtoMock);
        estoqueMock.setQuantidadeAtual(new BigDecimal("90"));

        ItemVenda itemEntidade = new ItemVenda();
        itemEntidade.setProduto(produtoMock);
        itemEntidade.setQuantidade(new BigDecimal("10"));

        Venda vendaEntidade = new Venda();
        vendaEntidade.setId(1L);
        vendaEntidade.setCancelada(false);
        vendaEntidade.setItensVenda(List.of(itemEntidade));

        when(vendaRepository.findByIdWithItens(1L)).thenReturn(Optional.of(vendaEntidade));
        when(estoqueRepository.findAllByProdutoIdIn(anySet())).thenReturn(List.of(estoqueMock));
        when(vendaRepository.save(vendaEntidade)).thenReturn(vendaEntidade);

        service.cancelVenda(1L);


        assertTrue(vendaEntidade.isCancelada());
        assertEquals(0, new BigDecimal("100").compareTo(estoqueMock.getQuantidadeAtual()));
        verify(vendaRepository).save(vendaEntidade);
    }

    @Test
    void deveLancarVendaJaCanceladaException() {
        Venda vendaJaCancelada = new Venda();
        vendaJaCancelada.setId(1L);
        vendaJaCancelada.setCancelada(true);

        when(vendaRepository.findByIdWithItens(1L)).thenReturn(Optional.of(vendaJaCancelada));

        assertThrows(
                VendaJaCanceladaException.class,
                () -> {
                    service.cancelVenda(1L);
                }
        );

        verify(vendaRepository, never()).save(any());
    }

}