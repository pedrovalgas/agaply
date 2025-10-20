package com.pedrolucas.Agaply.service;

import com.pedrolucas.Agaply.dto.produto.ProdutoRequestDTO;
import com.pedrolucas.Agaply.dto.produto.ProdutoResponseDTO;
import com.pedrolucas.Agaply.dto.produto.ProdutoUpdateDTO;
import com.pedrolucas.Agaply.exception.*;
import com.pedrolucas.Agaply.mapper.ProdutoMapper;
import com.pedrolucas.Agaply.repository.CategoriaRepository;
import com.pedrolucas.Agaply.repository.FornecedorRepository;
import com.pedrolucas.Agaply.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoMapper mapper;
    private final FornecedorRepository fornecedorRepository;
    private final CategoriaRepository categoriaRepository;


    @Transactional
    public ProdutoResponseDTO create(ProdutoRequestDTO dto){
        if (produtoRepository.existsByNome(dto.nome())){
            throw new ConflictException("Nome do produto já existe");
        }
        if (dto.quantidadeMinima() < 0){
            throw new InvalidFieldException("Quantidade mínima não pode ser menor que 0");
        }

        var fornecedor = fornecedorRepository.findById(dto.fornecedorId())
                .orElseThrow(() -> new FornecedorNotFoundException("Fornecedor não encontrado"));

        var categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new CategoriaNotFoundException("Categoria não encontrada"));


        var entity = mapper.toEntity(dto);
        entity.setFornecedor(fornecedor);
        entity.setCategoria(categoria);

        return mapper.toResponse(produtoRepository.save(entity));

    }

    public List<ProdutoResponseDTO> findAll(){
        return mapper.toResponseList(produtoRepository.findAll());
    }

    public ProdutoResponseDTO findById(Long id){
        var entity = produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNotFoundException("Produto não encontrado"));
        return mapper.toResponse(entity);
    }

    @Transactional
    public ProdutoResponseDTO update(Long id, ProdutoUpdateDTO dto){
        if (produtoRepository.existsByNomeAndIdNot(dto.nome(), id)) {
            throw new ConflictException("Nome do produto já existe");
        }
        if (dto.quantidadeMinima() < 0){
            throw new InvalidFieldException("Quantidade mínima não pode ser menor que 0");
        }

        var fornecedor = fornecedorRepository.findById(dto.fornecedorId())
                .orElseThrow(() -> new FornecedorNotFoundException("Fornecedor não encontrado"));

        var categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new CategoriaNotFoundException("Categoria não encontrada"));

        var entity = produtoRepository.findById(id)
                        .orElseThrow(() -> new ProdutoNotFoundException("Produto não encontrado"));


        mapper.toUpdate(dto, entity);
        entity.setFornecedor(fornecedor);
        entity.setCategoria(categoria);

        return mapper.toResponse(produtoRepository.save(entity));
    }

    @Transactional
    public void delete(Long id){
        var entity = produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNotFoundException("Produto não encontrado"));

        entity.setAtivo(false);
        produtoRepository.save(entity);
    }

}
