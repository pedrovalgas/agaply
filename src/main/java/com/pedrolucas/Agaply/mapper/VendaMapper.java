package com.pedrolucas.Agaply.mapper;

import com.pedrolucas.Agaply.dto.produto.ProdutoResponsDTO;
import com.pedrolucas.Agaply.dto.venda.VendaRequestDTO;
import com.pedrolucas.Agaply.dto.venda.VendaResponseDTO;
import com.pedrolucas.Agaply.model.Produto;
import com.pedrolucas.Agaply.model.Venda;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VendaMapper {

    Venda toEntity(VendaRequestDTO dto);

    VendaResponseDTO toResponse(Venda entity);

    List<VendaResponseDTO> toResponseList(List<Venda> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toUpdate(VendaResponseDTO dto, @MappingTarget Venda entity);

}
