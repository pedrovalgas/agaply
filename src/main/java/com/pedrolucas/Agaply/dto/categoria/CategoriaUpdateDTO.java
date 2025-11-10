package com.pedrolucas.Agaply.dto.categoria;

import jakarta.validation.constraints.NotBlank;

public record CategoriaUpdateDTO(

        @NotBlank(message = "O nome não pode ser nulo")
        String nome,

        String descricao) {
}
