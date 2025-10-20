package com.pedrolucas.Agaply.dto.estoque;

public record EstoqueRequestDTO(

        Long produtoId,

        int quantidadeAtual,

        int quantidadeMinima
) {
}
