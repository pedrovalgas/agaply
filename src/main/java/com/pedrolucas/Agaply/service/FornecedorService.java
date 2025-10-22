package com.pedrolucas.Agaply.service;

import com.pedrolucas.Agaply.dto.fornecedor.FornecedorRequestDTO;
import com.pedrolucas.Agaply.dto.fornecedor.FornecedorResponseDTO;
import com.pedrolucas.Agaply.dto.fornecedor.FornecedorUpdateDTO;
import com.pedrolucas.Agaply.exception.ConflictException;
import com.pedrolucas.Agaply.exception.FornecedorNotFoundException;
import com.pedrolucas.Agaply.mapper.FornecedorMapper;
import com.pedrolucas.Agaply.repository.FornecedorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FornecedorService {

    private final FornecedorRepository repository;
    private final FornecedorMapper mapper;

    @Transactional
    public FornecedorResponseDTO create(FornecedorRequestDTO dto){
        if (repository.existsByCnpj(dto.cnpj())){
            throw new ConflictException("CNPJ já cadastrado no sistema");
        }
        var entity = mapper.toEntity(dto);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<FornecedorResponseDTO> findAll(Pageable pageable){
        return repository.findAll(pageable)
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public FornecedorResponseDTO findById(Long id){
        var entity = repository.findById(id)
                .orElseThrow(() -> new FornecedorNotFoundException("Fornecedor não encontrado"));
        return mapper.toResponse(entity);
    }

    @Transactional
    public FornecedorResponseDTO update(Long id, FornecedorUpdateDTO dto){
        var entity = repository.findById(id)
                .orElseThrow(() -> new FornecedorNotFoundException("Fornecedor não encontrado"));
        if (repository.existsByCnpjAndIdNot(dto.cnpj(), id)){
            throw new ConflictException("CNPJ já cadastrado no sistema");
        }
        mapper.toUpdate(dto, entity);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(Long id){
        var entity = repository.findById(id)
                .orElseThrow(() -> new FornecedorNotFoundException("Fornecedor não encontrado"));
        entity.setAtivo(false);
        repository.save(entity);
    }

}
