package com.pedrolucas.Agaply.mapper;

import com.pedrolucas.Agaply.dto.estoque.EstoqueRequestDTO;
import com.pedrolucas.Agaply.dto.estoque.EstoqueResponseDTO;
import com.pedrolucas.Agaply.model.Estoque;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EstoqueMapper {

    Estoque toEntity(EstoqueRequestDTO dto);

    EstoqueResponseDTO toResponse(Estoque entity);

    List<EstoqueResponseDTO> toResponseList(List<Estoque> entities);
}
