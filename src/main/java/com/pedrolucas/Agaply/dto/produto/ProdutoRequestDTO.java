package com.pedrolucas.Agaply.dto.produto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoRequestDTO {

    private String nome;
    private String codigoDeBarras;
    private BigDecimal preco;
    private String descricao;
    private Long categoriaId;
    private Long fornecedorId;
    private BigDecimal quantidadeInicial;
    private BigDecimal quantidadeMinima;

}
