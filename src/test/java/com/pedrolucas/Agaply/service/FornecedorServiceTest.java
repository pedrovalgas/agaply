package com.pedrolucas.Agaply.service;

import com.pedrolucas.Agaply.dto.fornecedor.FornecedorRequestDTO;
import com.pedrolucas.Agaply.dto.fornecedor.FornecedorResponseDTO;
import com.pedrolucas.Agaply.dto.fornecedor.FornecedorUpdateDTO;
import com.pedrolucas.Agaply.exception.ConflictException;
import com.pedrolucas.Agaply.exception.FornecedorNotFoundException;
import com.pedrolucas.Agaply.mapper.FornecedorMapper;
import com.pedrolucas.Agaply.model.Fornecedor;
import com.pedrolucas.Agaply.repository.FornecedorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FornecedorServiceTest {


    @Mock
    private FornecedorRepository repository;

    @Mock
    private FornecedorMapper mapper;

    @InjectMocks
    private FornecedorService service;



    @Test
    void deveCriarFornecedorComSucesso() {
        FornecedorRequestDTO dto = new FornecedorRequestDTO("Ambev", "123", "999", "email@teste.com");

        Fornecedor fornecedorEntidade = new Fornecedor();
        fornecedorEntidade.setId(1L);
        fornecedorEntidade.setNome("Ambev");

        FornecedorResponseDTO responseDTO = new FornecedorResponseDTO(1L, "Ambev", "123", "999", "email@teste.com", true);

        when(repository.existsByCnpj("123")).thenReturn(false);
        when(mapper.toEntity(dto)).thenReturn(fornecedorEntidade);
        when(repository.save(fornecedorEntidade)).thenReturn(fornecedorEntidade);
        when(mapper.toResponse(fornecedorEntidade)).thenReturn(responseDTO);

        FornecedorResponseDTO resultado = service.create(dto);

        assertNotNull(resultado);
        assertEquals("Ambev", resultado.nome());
        assertEquals(1L, resultado.id());
        verify(repository).save(fornecedorEntidade);
    }


    @Test
    void deveLancarConflictExceptionAoCriarComCnpjDuplicado(){
        FornecedorRequestDTO dto = new FornecedorRequestDTO("Nome teste", "123456", "11111111111", "email@email.com");

        when(repository.existsByCnpj("123456")).thenReturn(true);

        ConflictException exception = assertThrows(
                ConflictException.class, () -> {service.create(dto);
                }
        );

        assertEquals("CNPJ já cadastrado no sistema", exception.getMessage());
        verify(repository, never()).save(any(Fornecedor.class));
    }

    @Test
    void deveBuscarFornecedorPorIdComSucesso() {
        Fornecedor fornecedorEntidade = new Fornecedor();
        fornecedorEntidade.setId(1L);
        FornecedorResponseDTO responseDTO = new FornecedorResponseDTO(1L, "Ambev", "123", "999", "email@teste.com", true);

        when(repository.findById(1L)).thenReturn(Optional.of(fornecedorEntidade));
        when(mapper.toResponse(fornecedorEntidade)).thenReturn(responseDTO);

        FornecedorResponseDTO resultado = service.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
    }

    @Test
    void deveLancarFornecedorNotFoundExceptionAoBuscarPorIdInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                FornecedorNotFoundException.class,
                () -> {
                    service.findById(99L);
                }
        );
    }

    @Test
    void deveAtualizarFornecedorComSucesso() {
        FornecedorUpdateDTO dto = new FornecedorUpdateDTO("Nome Novo", "123", "999", "email@teste.com");

        Fornecedor entidadeExistente = new Fornecedor();
        entidadeExistente.setId(1L);
        entidadeExistente.setNome("Nome Antigo");

        when(repository.findById(1L)).thenReturn(Optional.of(entidadeExistente));
        when(repository.existsByCnpjAndIdNot("123", 1L)).thenReturn(false);
        when(repository.save(entidadeExistente)).thenReturn(entidadeExistente);
        when(mapper.toResponse(entidadeExistente)).thenReturn(new FornecedorResponseDTO(1L, "Nome Novo", "123", "999", "email@teste.com", true));

        FornecedorResponseDTO resultado = service.update(1L, dto);

        assertNotNull(resultado);
        assertEquals("Nome Novo", resultado.nome());

        verify(mapper).toUpdate(dto, entidadeExistente);
        verify(repository).save(entidadeExistente);
    }

    @Test
    void deveLancarConflictExceptionAoAtualizarComCnpjDuplicado() {
        FornecedorUpdateDTO dto = new FornecedorUpdateDTO("Nome Novo", "123", "999", "email@teste.com");
        Fornecedor entidadeExistente = new Fornecedor();
        entidadeExistente.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(entidadeExistente));
        when(repository.existsByCnpjAndIdNot("123", 1L)).thenReturn(true);

        // 2. ACT & 3. ASSERT
        assertThrows(
                ConflictException.class,
                () -> {
                    service.update(1L, dto);
                }
        );

        verify(repository, never()).save(any(Fornecedor.class));
    }

    @Test
    void deveDesativarFornecedorComSucesso() {
        Fornecedor entidadeExistente = new Fornecedor();
        entidadeExistente.setId(1L);
        entidadeExistente.setAtivo(true);

        when(repository.findById(1L)).thenReturn(Optional.of(entidadeExistente));

        service.delete(1L);

        verify(repository).save(entidadeExistente);
        assertFalse(entidadeExistente.isAtivo());
    }

}