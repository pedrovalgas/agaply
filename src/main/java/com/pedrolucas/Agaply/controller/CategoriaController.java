package com.pedrolucas.Agaply.controller;

import com.pedrolucas.Agaply.dto.categoria.CategoriaRequestDTO;
import com.pedrolucas.Agaply.dto.categoria.CategoriaResponseDTO;
import com.pedrolucas.Agaply.dto.categoria.CategoriaUpdateDTO;
import com.pedrolucas.Agaply.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService service;

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> create(@RequestBody @Valid CategoriaRequestDTO dto){
        CategoriaResponseDTO response = service.create(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<CategoriaResponseDTO>> findAll(Pageable pageable){
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> update(@PathVariable Long id, @RequestBody @Valid CategoriaUpdateDTO dto){
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
