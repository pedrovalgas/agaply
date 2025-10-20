package com.pedrolucas.Agaply.dto.fornecedor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record FornecedorRequestDTO (

        @NotBlank
        String nome,

        @NotBlank
        String cnpj,

        @NotBlank
        String telefone,

        @NotBlank
        @Email
        String email
){
}
