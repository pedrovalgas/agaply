package com.pedrolucas.Agaply.repository;

import com.pedrolucas.Agaply.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    boolean existsByNome(String nome);

    boolean existsByNomeAndIdNot(String nome, Long id);

    Optional<Produto> findByNome(String nome);
}
