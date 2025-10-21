package com.pedrolucas.Agaply.dto.venda;

import com.pedrolucas.Agaply.dto.itemvenda.ItemVendaResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VendaResponseDTO(
        Long id,
        LocalDateTime dataHora,
        BigDecimal valorTotal,
        boolean cancelada,
        List<ItemVendaResponseDTO> itens
) {
}
