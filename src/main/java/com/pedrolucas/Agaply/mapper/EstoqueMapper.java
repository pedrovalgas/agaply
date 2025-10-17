package com.pedrolucas.Agaply.mapper;

import com.pedrolucas.Agaply.dto.estoque.EstoqueResponseDTO;
import com.pedrolucas.Agaply.model.Estoque;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EstoqueMapper {

    EstoqueResponseDTO toResponse(Estoque entity);
}
