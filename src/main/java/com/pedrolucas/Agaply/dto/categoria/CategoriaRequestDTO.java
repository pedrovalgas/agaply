package com.pedrolucas.Agaply.dto.categoria;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequestDTO(

        @NotBlank
        String nome,

        String descricao){}
