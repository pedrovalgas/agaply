package com.pedrolucas.Agaply.dto.fornecedor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FornecedorRequestDTO {

    private String nome;
    private String cnpj;
    private String telefone;
    private String email;

}
