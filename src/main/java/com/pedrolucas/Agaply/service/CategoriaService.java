package com.pedrolucas.Agaply.service;

import com.pedrolucas.Agaply.dto.categoria.CategoriaRequestDTO;
import com.pedrolucas.Agaply.dto.categoria.CategoriaResponseDTO;
import com.pedrolucas.Agaply.dto.categoria.CategoriaUpdateDTO;
import com.pedrolucas.Agaply.exception.CategoriaNotFoundException;
import com.pedrolucas.Agaply.mapper.CategoriaMapper;
import com.pedrolucas.Agaply.repository.CategoriaRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository repository;
    private final CategoriaMapper mapper;


    @Transactional
    public CategoriaResponseDTO create(CategoriaRequestDTO dto){
        var entity = mapper.toEntity(dto);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> findAll(){
        return mapper.toResponseList(repository.findAll());
    }

    @Transactional(readOnly = true)
    public CategoriaResponseDTO findById(Long id){
        var entity = repository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException("Categoria não encontrada"));
        return mapper.toResponse(entity);
    }

    @Transactional
    public CategoriaResponseDTO update(Long id, CategoriaUpdateDTO dto){
        var entity = repository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException("Categoria não encontrada"));
        mapper.toUpdate(dto, entity);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(Long id){
        var entity = repository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException("Categoria não encontrada"));
        repository.delete(entity);
    }

}
