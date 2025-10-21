package com.pedrolucas.Agaply.dto.itemvenda;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

public record ItemVendaRequestDTO(
        Long produtoId,
        int quantidade
) {
}
