package com.pedrolucas.Agaply.dto.estoque;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record EstoqueRequestDTO(

        @NotNull
        Long produtoId,

        @NotNull
        BigDecimal quantidadeAtual,

        @NotNull
        BigDecimal quantidadeMinima
) {
}
