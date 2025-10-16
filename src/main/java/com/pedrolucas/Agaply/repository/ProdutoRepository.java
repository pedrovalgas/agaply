package com.pedrolucas.Agaply.repository;

import com.pedrolucas.Agaply.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
