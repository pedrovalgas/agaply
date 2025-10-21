package com.pedrolucas.Agaply.service;

import com.pedrolucas.Agaply.dto.itemvenda.ItemVendaResponseDTO;
import com.pedrolucas.Agaply.exception.ItemVendaNotFoundException;
import com.pedrolucas.Agaply.exception.VendaNotFoundException;
import com.pedrolucas.Agaply.mapper.ItemVendaMapper;
import com.pedrolucas.Agaply.model.ItemVenda;
import com.pedrolucas.Agaply.repository.EstoqueRepository;
import com.pedrolucas.Agaply.repository.ItemVendaRepository;
import com.pedrolucas.Agaply.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemVendaService {

    private final ItemVendaRepository itemVendaRepository;
    private final VendaRepository vendaRepository;
    private final ItemVendaMapper mapper;

    @Transactional(readOnly = true)
    public ItemVendaResponseDTO findById(Long id) {
        return itemVendaRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ItemVendaNotFoundException("Item de Venda não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<ItemVendaResponseDTO> findAllByVendaId(Long vendaId) {
        if (!vendaRepository.existsById(vendaId)) {
            throw new VendaNotFoundException("Venda não encontrada: " + vendaId);
        }

        List<ItemVenda> itens = itemVendaRepository.findAllByVendaId(vendaId);

        return mapper.toResponseList(itens);
    }

}
