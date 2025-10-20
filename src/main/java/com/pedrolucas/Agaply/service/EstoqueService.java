package com.pedrolucas.Agaply.service;

import com.pedrolucas.Agaply.dto.estoque.EstoqueRequestDTO;
import com.pedrolucas.Agaply.dto.estoque.EstoqueResponseDTO;
import com.pedrolucas.Agaply.exception.ConflictException;
import com.pedrolucas.Agaply.exception.EstoqueNotFoundException;
import com.pedrolucas.Agaply.exception.ProdutoNotFoundException;
import com.pedrolucas.Agaply.mapper.EstoqueMapper;
import com.pedrolucas.Agaply.repository.EstoqueRepository;
import com.pedrolucas.Agaply.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final EstoqueMapper mapper;
    private final ProdutoRepository produtoRepository;

    @Transactional
    public EstoqueResponseDTO create(EstoqueRequestDTO dto) {
        var produto = produtoRepository.findById(dto.produtoId())
                .orElseThrow(() -> new ProdutoNotFoundException("Produto não encontrado"));

        if (estoqueRepository.existsByProdutoId(dto.produtoId())) {
            throw new ConflictException("Já existe um estoque cadastrado para este produto");
        }
        if (dto.quantidadeAtual() < 0 || dto.quantidadeMinima() < 0) {
            throw new IllegalArgumentException("A quantidade não pode ser negativa");
        }

        var entity = mapper.toEntity(dto);
        entity.setProduto(produto);

        return mapper.toResponse(estoqueRepository.save(entity));
    }

    public EstoqueResponseDTO findByProdutoId(Long produtoId) {
        var entity = estoqueRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new EstoqueNotFoundException("Estoque não encontrado para este produto"));
        return mapper.toResponse(entity);
    }

    @Transactional
    public EstoqueResponseDTO updateQuantidade(Long produtoId, int novaQuantidade) {
        var entity = estoqueRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new EstoqueNotFoundException("Estoque não encontrado para este produto"));

        if (novaQuantidade < 0) {
            throw new IllegalArgumentException("A quantidade não pode ser negativa");
        }

        entity.setQuantidadeAtual(novaQuantidade);
        return mapper.toResponse(estoqueRepository.save(entity));
    }

    @Transactional
    public void deleteByProdutoId(Long produtoId) {
        var entity = estoqueRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new EstoqueNotFoundException("Estoque não encontrado para este produto"));

        estoqueRepository.delete(entity);
    }

    public List<EstoqueResponseDTO> findAll() {
        return mapper.toResponseList(estoqueRepository.findAll());
    }
}
