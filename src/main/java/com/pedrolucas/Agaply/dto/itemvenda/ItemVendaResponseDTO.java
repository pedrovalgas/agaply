package com.pedrolucas.Agaply.dto.itemvenda;

import java.math.BigDecimal;

public record ItemVendaResponseDTO(
        Long id,
        Long produtoId,
        String nomeProduto,
        BigDecimal quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal
) {
}
