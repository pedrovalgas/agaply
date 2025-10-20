package com.pedrolucas.Agaply.dto.produto;


import java.math.BigDecimal;

public record ProdutoUpdateDTO(
        String nome,

        String codigoDeBarras,

        BigDecimal preco,

        String descricao,

        Long categoriaId,

        Long fornecedorId
) {
}
