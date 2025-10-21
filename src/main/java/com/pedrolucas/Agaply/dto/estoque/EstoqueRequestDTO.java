package com.pedrolucas.Agaply.dto.estoque;

import java.math.BigDecimal;

public record EstoqueRequestDTO(

        Long produtoId,

        BigDecimal quantidadeAtual,

        BigDecimal quantidadeMinima
) {
}
