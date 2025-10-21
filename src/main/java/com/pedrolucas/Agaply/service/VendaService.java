package com.pedrolucas.Agaply.service;

import com.pedrolucas.Agaply.dto.venda.VendaRequestDTO;
import com.pedrolucas.Agaply.dto.venda.VendaResponseDTO;
import com.pedrolucas.Agaply.exception.EstoqueInsuficienteException;
import com.pedrolucas.Agaply.exception.EstoqueNotFoundException;
import com.pedrolucas.Agaply.exception.VendaJaCanceladaException;
import com.pedrolucas.Agaply.exception.VendaNotFoundException;
import com.pedrolucas.Agaply.mapper.VendaMapper;
import com.pedrolucas.Agaply.model.Estoque;
import com.pedrolucas.Agaply.model.ItemVenda;
import com.pedrolucas.Agaply.model.Produto;
import com.pedrolucas.Agaply.model.Venda;
import com.pedrolucas.Agaply.repository.EstoqueRepository;
import com.pedrolucas.Agaply.repository.ProdutoRepository;
import com.pedrolucas.Agaply.repository.VendaRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final VendaMapper mapper;
    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;


    @Transactional
    public VendaResponseDTO create(VendaRequestDTO dto){
        //Mapeia o DTO para a entidade e inicializa o total
        var entity = mapper.toEntity(dto);
        entity.setValorTotal(BigDecimal.ZERO);

        //Salva a Venda (sem itens) para pegar o ID
        Venda vendaSalva = vendaRepository.save(entity);

        //Coleta IDs para busca
        Set<Long> idsDosProdutos = entity.getItensVenda().stream()
                .map(item -> item.getProduto().getId())
                .collect(Collectors.toSet());

        //Busca todos os dados necessários (Estoque e Produto) em consultas únicas
        Map<Long, Estoque> mapaDeEstoque = estoqueRepository.findAllById(idsDosProdutos).stream()
                .collect(Collectors.toMap(Estoque::getId, Function.identity()));

        Map<Long, Produto> mapaDeProdutos = produtoRepository.findAllById(idsDosProdutos).stream()
                .collect(Collectors.toMap(Produto::getId, Function.identity()));

        //Garante que o estoque está disponível ANTES de processar
        for (ItemVenda item : entity.getItensVenda()) {
            Long produtoId = item.getProduto().getId();
            Estoque estoqueDoProduto = mapaDeEstoque.get(produtoId);

            if (estoqueDoProduto == null) {
                throw new EstoqueNotFoundException("Estoque não encontrado");
            }

            // Verifica se tem
            if (estoqueDoProduto.getQuantidadeAtual().compareTo(item.getQuantidade()) < 0) {
                throw new EstoqueInsuficienteException("Estoque insuficiente para o produto ID: " + produtoId);
            }
        }

        // Se todas as validações passaram, processa os itens.
        BigDecimal totalDaVenda = BigDecimal.ZERO;

        for (ItemVenda item : entity.getItensVenda()) {
            Long produtoId = item.getProduto().getId();
            Produto produto = mapaDeProdutos.get(produtoId);
            Estoque estoque = mapaDeEstoque.get(produtoId);

            //Cálculo de Preço
            BigDecimal precoUnitario = produto.getPreco();
            BigDecimal subtotal = precoUnitario.multiply(item.getQuantidade());
            item.setPrecoUnitario(precoUnitario);
            item.setSubtotal(subtotal);

            // Acumula o total
            totalDaVenda = totalDaVenda.add(subtotal);

            //Baixa de Estoque (em memória)
            BigDecimal novaQuantidade = estoque.getQuantidadeAtual().subtract(item.getQuantidade());
            estoque.setQuantidadeAtual(novaQuantidade);

            //Associa o item à Venda (que já tem ID)
            item.setVenda(vendaSalva);
        }

        //Atualiza a entidade principal com o total e os itens processados
        vendaSalva.setValorTotal(totalDaVenda);
        vendaSalva.setItensVenda(entity.getItensVenda());

        //Persiste as alterações.
        Venda vendaCompleta = vendaRepository.save(vendaSalva);

        return mapper.toResponse(vendaCompleta);
    }

    @Transactional(readOnly = true)
    public VendaResponseDTO findById(Long id) {
        var entity = vendaRepository.findByIdWithItens(id)
                .orElseThrow(() -> new VendaNotFoundException("Venda não encontrada: " + id));
        return mapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> findAll(){
        return mapper.toResponseList(vendaRepository.findAll());
    }

    @Transactional
    public VendaResponseDTO cancelVenda(Long id) {
        //Busca a Venda e seus itens
        var entity = vendaRepository.findByIdWithItens(id)
                .orElseThrow(() -> new VendaNotFoundException("Venda não encontrada"));

        //Regra de negócio: Não cancelar uma venda que já foi cancelada
        if (entity.isCancelada()) {
            throw new VendaJaCanceladaException("Esta venda já está cancelada.");
        }


        //Coleta IDs para busca(igual ao create)
        Set<Long> idsDosProdutos = entity.getItensVenda().stream()
                .map(item -> item.getProduto().getId())
                .collect(Collectors.toSet());

        //Busca os estoques (igual ao create)
        Map<Long, Estoque> mapaDeEstoque = estoqueRepository.findAllById(idsDosProdutos).stream()
                .collect(Collectors.toMap(Estoque::getId, Function.identity()));

        //Loop para DEVOLVER o estoque
        for (ItemVenda item : entity.getItensVenda()) {
            Long produtoId = item.getProduto().getId();
            Estoque estoque = mapaDeEstoque.get(produtoId);

            // Só devolve se o registro de estoque ainda existir
            if (estoque == null) {
                throw new EstoqueNotFoundException(
                        "Impossível cancelar: Produto ID " + produtoId + " não possui mais registro de estoque."
                );
            }
            BigDecimal novaQuantidade = estoque.getQuantidadeAtual().add(item.getQuantidade());
            estoque.setQuantidadeAtual(novaQuantidade);
        }

        //Marca a Venda como cancelada
        entity.setCancelada(true);

        // O estoque será salvo junto pela transação.
        Venda vendaCancelada = vendaRepository.save(entity);

        return mapper.toResponse(vendaCancelada);
    }

}