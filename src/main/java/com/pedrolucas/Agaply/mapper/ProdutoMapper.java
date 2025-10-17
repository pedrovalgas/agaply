package com.pedrolucas.Agaply.mapper;

import com.pedrolucas.Agaply.dto.produto.ProdutoRequestDTO;
import com.pedrolucas.Agaply.dto.produto.ProdutoResponsDTO;
import com.pedrolucas.Agaply.model.Produto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProdutoMapper{

    Produto toEntity(ProdutoRequestDTO dto);

    ProdutoResponsDTO toResponse(Produto entity);

    List<ProdutoResponsDTO> toResponseList(List<Produto> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toUpdate(ProdutoResponsDTO dto, @MappingTarget Produto entity);

}

