package com.pedrolucas.Agaply.mapper;

import com.pedrolucas.Agaply.dto.fornecedor.FornecedorRequestDTO;
import com.pedrolucas.Agaply.dto.fornecedor.FornecedorResponseDTO;
import com.pedrolucas.Agaply.model.Fornecedor;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FornecedorMapper {

    Fornecedor toEntity(FornecedorRequestDTO dto);

    FornecedorResponseDTO toResponse(Fornecedor entity);

    List<FornecedorResponseDTO> toResponseList(List<Fornecedor> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toUpdate(FornecedorResponseDTO dto, @MappingTarget Fornecedor entity);
}
