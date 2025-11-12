package com.pedrolucas.Agaply.dto.itemvenda;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

public record ItemVendaRequestDTO(

        @NotNull
        Long produtoId,

        @NotNull
        @Positive
        int quantidade
) {
}
