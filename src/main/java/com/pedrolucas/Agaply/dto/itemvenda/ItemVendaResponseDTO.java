package com.pedrolucas.Agaply.dto.itemvenda;

import java.math.BigDecimal;

public record ItemVendaResponseDTO(
        Long id,
        String produtoNome,
        int quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal
) {
}
