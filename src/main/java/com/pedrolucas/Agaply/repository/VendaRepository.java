package com.pedrolucas.Agaply.repository;

import com.pedrolucas.Agaply.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VendaRepository extends JpaRepository<Venda, Long> {
    @Query("SELECT v FROM Venda v JOIN FETCH v.itensVenda WHERE v.id = :id")
    Optional<Venda> findByIdWithItens(Long id);
}
