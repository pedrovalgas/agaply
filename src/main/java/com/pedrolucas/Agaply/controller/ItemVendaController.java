package com.pedrolucas.Agaply.controller;

import com.pedrolucas.Agaply.dto.itemvenda.ItemVendaResponseDTO;
import com.pedrolucas.Agaply.service.ItemVendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itens-venda")
@RequiredArgsConstructor
public class ItemVendaController {

    private final ItemVendaService service;

    @GetMapping("/{id}")
    public ResponseEntity<ItemVendaResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/venda/{vendaId}")
    public ResponseEntity<List<ItemVendaResponseDTO>> findAllByVendaId(@PathVariable Long vendaId) {
        return ResponseEntity.ok(service.findAllByVendaId(vendaId));
    }
}
