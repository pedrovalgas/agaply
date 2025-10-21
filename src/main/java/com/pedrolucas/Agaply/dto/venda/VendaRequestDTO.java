package com.pedrolucas.Agaply.dto.venda;

import com.pedrolucas.Agaply.dto.itemvenda.ItemVendaRequestDTO;
import com.pedrolucas.Agaply.model.ItemVenda;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public record VendaRequestDTO (

        List<ItemVenda> itens,

        LocalDateTime data


){
}
