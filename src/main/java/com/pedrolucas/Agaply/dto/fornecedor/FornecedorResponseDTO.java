package com.pedrolucas.Agaply.dto.fornecedor;

public record FornecedorResponseDTO(
        Long id,
        String nome,
        String cnpj,
        String telefone,
        String email,
        boolean ativo
) {
}
