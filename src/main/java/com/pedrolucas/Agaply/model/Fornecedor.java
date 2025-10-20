package com.pedrolucas.Agaply.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "fornecedor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String cnpj;

    private String telefone;

    private String email;

    @OneToMany(mappedBy = "fornecedor")
    private List<Produto> produtos;

    private boolean ativo;

}
