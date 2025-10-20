package com.pedrolucas.Agaply.dto.produto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProdutoResponseDTO(
        Long id,

        String nome,

        String codigoDeBarras,

        BigDecimal preco,

        String descricao,

        String categoria,

        String fornecedor,

        int quantidadeMinima,

        int quantidadeAtual
) {
}
