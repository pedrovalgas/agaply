package com.pedrolucas.Agaply.dto.produto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

public record ProdutoRequestDTO(

        @NotBlank
        String nome,

        String codigoDeBarras,

        @NotNull
        BigDecimal preco,

        String descricao,

        @NotNull
        Long categoriaId,

        @NotNull
        Long fornecedorId
) {
}
