package com.pedrolucas.Agaply.mapper;

import com.pedrolucas.Agaply.dto.produto.ProdutoRequestDTO;
import com.pedrolucas.Agaply.dto.produto.ProdutoResponseDTO;
import com.pedrolucas.Agaply.dto.produto.ProdutoUpdateDTO;
import com.pedrolucas.Agaply.model.Produto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProdutoMapper{

    Produto toEntity(ProdutoRequestDTO dto);

    ProdutoResponseDTO toResponse(Produto entity);

    List<ProdutoResponseDTO> toResponseList(List<Produto> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toUpdate(ProdutoUpdateDTO dto, @MappingTarget Produto entity);

}

