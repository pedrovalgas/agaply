package com.pedrolucas.Agaply.mapper;

import com.pedrolucas.Agaply.dto.categoria.CategoriaRequestDTO;
import com.pedrolucas.Agaply.dto.categoria.CategoriaResponseDTO;
import com.pedrolucas.Agaply.dto.categoria.CategoriaUpdateDTO;
import com.pedrolucas.Agaply.model.Categoria;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    Categoria toEntity(CategoriaRequestDTO dto);

    CategoriaResponseDTO toResponse(Categoria entity);

    List<CategoriaResponseDTO> toResponseList(List<Categoria> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toUpdate(CategoriaUpdateDTO dto, @MappingTarget Categoria entity);
}
