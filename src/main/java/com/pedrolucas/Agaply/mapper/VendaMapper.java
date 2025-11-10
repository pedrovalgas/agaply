package com.pedrolucas.Agaply.mapper;

import com.pedrolucas.Agaply.dto.itemvenda.ItemVendaRequestDTO;
import com.pedrolucas.Agaply.dto.itemvenda.ItemVendaResponseDTO;
import com.pedrolucas.Agaply.dto.venda.VendaRequestDTO;
import com.pedrolucas.Agaply.dto.venda.VendaResponseDTO;
import com.pedrolucas.Agaply.dto.venda.VendaUpdateDTO;
import com.pedrolucas.Agaply.model.ItemVenda;
import com.pedrolucas.Agaply.model.Produto;
import com.pedrolucas.Agaply.model.Venda;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VendaMapper {

    @Mapping(source = "itens", target = "itensVenda")
    Venda toEntity(VendaRequestDTO dto);

    @Mapping(source = "itensVenda", target = "itens")
    VendaResponseDTO toResponse(Venda entity);

    List<VendaResponseDTO> toResponseList(List<Venda> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "itens", target = "itensVenda")
    void toUpdate(VendaUpdateDTO dto, @MappingTarget Venda entity);

    @Mapping(source = "produtoId", target = "produto")
    ItemVenda itemVendaDtoToItemVenda(ItemVendaRequestDTO itemDto);

    @Mapping(source = "produto.id", target = "produtoId")
    @Mapping(source = "produto.nome", target = "nomeProduto")
    ItemVendaResponseDTO itemVendaToItemVendaResponseDTO(ItemVenda item);

    default Produto map(Long produtoId) {
        if (produtoId == null) {
            return null;
        }
        Produto produto = new Produto();
        produto.setId(produtoId);
        return produto;
    }

}
