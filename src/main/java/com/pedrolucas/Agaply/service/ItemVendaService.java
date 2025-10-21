package com.pedrolucas.Agaply.service;

import com.pedrolucas.Agaply.mapper.ItemVendaMapper;
import com.pedrolucas.Agaply.repository.EstoqueRepository;
import com.pedrolucas.Agaply.repository.ItemVendaRepository;
import com.pedrolucas.Agaply.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemVendaService {

    private final ItemVendaRepository itemVendaRepository;
    private final ItemVendaMapper mapper;
    private final VendaRepository vendaRepository;
    private final EstoqueRepository estoqueRepository;





}
