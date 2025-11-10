package com.pedrolucas.Agaply.mapper;

import com.pedrolucas.Agaply.dto.estoque.EstoqueRequestDTO;
import com.pedrolucas.Agaply.dto.estoque.EstoqueResponseDTO;
import com.pedrolucas.Agaply.model.Estoque;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EstoqueMapper {

    Estoque toEntity(EstoqueRequestDTO dto);

    @Mapping(source = "produto.id", target = "produtoId")
    EstoqueResponseDTO toResponse(Estoque entity);

    List<EstoqueResponseDTO> toResponseList(List<Estoque> entities);
}
