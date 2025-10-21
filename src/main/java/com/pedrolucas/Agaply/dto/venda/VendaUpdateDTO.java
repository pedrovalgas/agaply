package com.pedrolucas.Agaply.dto.venda;

import com.pedrolucas.Agaply.model.ItemVenda;

import java.time.LocalDateTime;
import java.util.List;

public record VendaUpdateDTO(

        List<ItemVenda> itens,

        LocalDateTime datahora


) {
}
