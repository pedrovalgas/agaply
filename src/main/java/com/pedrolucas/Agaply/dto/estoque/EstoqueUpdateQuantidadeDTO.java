package com.pedrolucas.Agaply.dto.estoque;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record EstoqueUpdateQuantidadeDTO(
        @NotNull
        @PositiveOrZero
        BigDecimal novaQuantidade
) {
}
