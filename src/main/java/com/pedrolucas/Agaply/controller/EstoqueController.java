package com.pedrolucas.Agaply.controller;

import com.pedrolucas.Agaply.dto.estoque.EstoqueUpdateQuantidadeDTO;
import com.pedrolucas.Agaply.dto.estoque.EstoqueRequestDTO;
import com.pedrolucas.Agaply.dto.estoque.EstoqueResponseDTO;
import com.pedrolucas.Agaply.service.EstoqueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/estoques")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService service;


    @PostMapping
    public ResponseEntity<EstoqueResponseDTO> create(@RequestBody @Valid EstoqueRequestDTO dto){
        EstoqueResponseDTO response = service.create(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<EstoqueResponseDTO>> findAll(Pageable pageable){
        return ResponseEntity.ok(service.findAll(pageable));
    }


    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<EstoqueResponseDTO> findById(@PathVariable Long produtoId){
        return ResponseEntity.ok(service.findByProdutoId(produtoId));
    }

    @PutMapping("/produto/{produtoId}")
    public ResponseEntity<EstoqueResponseDTO> update(@PathVariable Long produtoId, @RequestBody @Valid EstoqueUpdateQuantidadeDTO dto){
        EstoqueResponseDTO response = service.updateQuantidade(produtoId, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/produto/{produtoId}")
    public ResponseEntity<Void> delete(@PathVariable Long produtoId){
        service.deleteByProdutoId(produtoId);
        return ResponseEntity.noContent().build();
    }

}
