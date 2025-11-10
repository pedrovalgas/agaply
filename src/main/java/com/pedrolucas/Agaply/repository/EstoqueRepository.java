package com.pedrolucas.Agaply.repository;

import com.pedrolucas.Agaply.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    boolean existsByProdutoId(Long aLong);

    Optional<Estoque> findByProdutoId(Long produtoId);

    List<Estoque> findAllByProdutoIdIn(Collection<Long> produtoIds);
}
