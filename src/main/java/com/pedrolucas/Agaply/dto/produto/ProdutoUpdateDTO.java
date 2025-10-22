package com.pedrolucas.Agaply.dto.produto;


import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProdutoUpdateDTO(
        String nome,

        String codigoDeBarras,

        @PositiveOrZero
        BigDecimal preco,

        String descricao,

        Long categoriaId,

        Long fornecedorId,

        int quantidadeMinima,

        int quantidadeAtual
) {
}
