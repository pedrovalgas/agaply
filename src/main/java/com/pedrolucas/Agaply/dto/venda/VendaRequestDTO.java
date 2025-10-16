package com.pedrolucas.Agaply.dto.venda;

import com.pedrolucas.Agaply.dto.itemvenda.ItemVendaRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VendaRequestDTO {
    private List<ItemVendaRequestDTO> itens;
}
