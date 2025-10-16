package com.pedrolucas.Agaply.dto.itemvenda;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemVendaRequestDTO {

    private Long produtoId;
    private BigDecimal quantidade;

}
