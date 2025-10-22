package com.pedrolucas.Agaply.repository;

import com.pedrolucas.Agaply.model.ItemVenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemVendaRepository extends JpaRepository<ItemVenda, Long> {
    List<ItemVenda> findAllByVendaId(Long vendaId);
}
