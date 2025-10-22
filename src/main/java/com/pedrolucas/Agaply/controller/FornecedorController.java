package com.pedrolucas.Agaply.controller;

import com.pedrolucas.Agaply.dto.fornecedor.FornecedorRequestDTO;
import com.pedrolucas.Agaply.dto.fornecedor.FornecedorResponseDTO;
import com.pedrolucas.Agaply.dto.fornecedor.FornecedorUpdateDTO;
import com.pedrolucas.Agaply.service.FornecedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/fornecedores")
@RequiredArgsConstructor
public class FornecedorController {

    private final FornecedorService service;


    @PostMapping
    public ResponseEntity<FornecedorResponseDTO> create(@RequestBody @Valid FornecedorRequestDTO dto){
        FornecedorResponseDTO response = service.create(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<FornecedorResponseDTO>> findAll(Pageable pageable){
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FornecedorResponseDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FornecedorResponseDTO> update(@PathVariable Long id, @RequestBody @Valid FornecedorUpdateDTO dto){
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
