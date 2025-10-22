package com.pedrolucas.Agaply.controller;


import com.pedrolucas.Agaply.dto.venda.VendaRequestDTO;
import com.pedrolucas.Agaply.dto.venda.VendaResponseDTO;
import com.pedrolucas.Agaply.service.VendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/vendas")
@RequiredArgsConstructor
public class VendaController {

    private final VendaService service;

    @PostMapping
    public ResponseEntity<VendaResponseDTO> create(@RequestBody @Valid VendaRequestDTO dto){
        VendaResponseDTO response = service.create(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<VendaResponseDTO>> findAll(Pageable pageable){
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendaResponseDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok(service.findById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VendaResponseDTO> cancelVenda(@PathVariable Long id){
        return ResponseEntity.ok(service.cancelVenda(id));
    }


}
