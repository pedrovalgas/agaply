package com.pedrolucas.Agaply.mapper;

import com.pedrolucas.Agaply.dto.itemvenda.ItemVendaRequestDTO;
import com.pedrolucas.Agaply.dto.itemvenda.ItemVendaResponseDTO;
import com.pedrolucas.Agaply.model.ItemVenda;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemVendaMapper {

    ItemVenda toEntity(ItemVendaRequestDTO dto);

    ItemVendaResponseDTO toResponse(ItemVenda entity);

    List<ItemVendaResponseDTO> toResponseList(List<ItemVenda> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toUpdate(ItemVendaResponseDTO dto, @MappingTarget ItemVenda entity);

}
