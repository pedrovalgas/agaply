package com.pedrolucas.Agaply.dto.estoque;

import java.math.BigDecimal;

public record EstoqueResponseDTO(
        Long id,
        int quantidadeAtual,
        int quantidadeMinima,
        Long produtoId
) {
}
