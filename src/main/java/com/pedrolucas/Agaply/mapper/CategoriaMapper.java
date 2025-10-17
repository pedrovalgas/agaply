package com.pedrolucas.Agaply.mapper;

import com.pedrolucas.Agaply.dto.categoria.CategoriaRequestDTO;
import com.pedrolucas.Agaply.dto.categoria.CategoriaResponseDTO;
import com.pedrolucas.Agaply.model.Categoria;
import com.pedrolucas.Agaply.model.Produto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    Categoria toEntity(CategoriaRequestDTO dto);

    CategoriaResponseDTO toResponse(Categoria entity);

    List<CategoriaResponseDTO> toResponseList(List<Categoria> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toUpdate(CategoriaResponseDTO dto, @MappingTarget Categoria entity);
}
