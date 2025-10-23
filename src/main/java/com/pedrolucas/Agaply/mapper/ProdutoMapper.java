package com.pedrolucas.Agaply.mapper;

import com.pedrolucas.Agaply.dto.produto.ProdutoRequestDTO;
import com.pedrolucas.Agaply.dto.produto.ProdutoResponseDTO;
import com.pedrolucas.Agaply.dto.produto.ProdutoUpdateDTO;
import com.pedrolucas.Agaply.model.Produto;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProdutoMapper{

    Produto toEntity(ProdutoRequestDTO dto);

    @Mapping(target = "fornecedor", source = "fornecedor.nome")
    @Mapping(target = "categoria", source = "categoria.nome")
    ProdutoResponseDTO toResponse(Produto entity);

    List<ProdutoResponseDTO> toResponseList(List<Produto> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toUpdate(ProdutoUpdateDTO dto, @MappingTarget Produto entity);

}

