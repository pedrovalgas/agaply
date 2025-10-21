package com.pedrolucas.Agaply.dto.estoque;

import java.math.BigDecimal;

public record EstoqueResponseDTO(
        Long id,
        BigDecimal quantidadeAtual,
        BigDecimal quantidadeMinima,
        Long produtoId
) {
}
