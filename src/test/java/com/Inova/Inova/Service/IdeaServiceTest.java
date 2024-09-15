package com.Inova.Inova.Service;

import com.Inova.Inova.Entities.Enum.Role;
import com.Inova.Inova.Entities.IdeaEntity;
import com.Inova.Inova.Entities.UserEntity;
import com.Inova.Inova.Repository.IdeaRepository;
import com.Inova.Inova.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class IdeaServiceTest {

    @MockBean
    IdeaRepository ideaRepository;

    @MockBean
    UserRepository userRepository;

    @Autowired
    IdeaService ideaService;

    @BeforeEach
    void setup() {
        List<IdeaEntity> lista = new ArrayList<>();
        lista.add(new IdeaEntity(UUID.randomUUID(), "Ideia", "Impacto", new BigDecimal("1000.00"), "Descricao", null));
        lista.add(new IdeaEntity(UUID.randomUUID(), "Ideia2", "Impacto2", new BigDecimal("1000.00"), "Descricao2", null));
        lista.add(new IdeaEntity(UUID.randomUUID(), "Ideia3", "Impacto3", new BigDecimal("1000.00"), "Descricao3", null));
        when(ideaRepository.findAll()).thenReturn(lista);
    }

    @Test
    void postarIdeia() {
        UserEntity colaborador1 = new UserEntity(UUID.randomUUID(), "teste1", "teste1@gmail.com", "senha", Role.COLABORADOR, null, null);
        UserEntity colaborador2 = new UserEntity(UUID.randomUUID(), "teste2", "teste2@gmail.com", "senha", Role.COLABORADOR, null, null);
        IdeaEntity ideia = new IdeaEntity(UUID.randomUUID(), "Ideia", "Impacto", new BigDecimal("1000.00"), "Descricao", null);

        when(userRepository.findById(colaborador1.getId())).thenReturn(Optional.of(colaborador1));
        when(userRepository.findById(colaborador2.getId())).thenReturn(Optional.of(colaborador2));
        when(ideaRepository.save(ideia)).thenReturn(ideia);

        ideia.setColaboradores(Set.of(colaborador1, colaborador2));

        var resultado = ideaService.postarIdeia(ideia);

        assertEquals("Ideia", resultado.getNome());
        assertEquals(2, resultado.getColaboradores().size());
    }

    @Test
    void findAll() {
        var resultado = ideaService.findAll();

        assertEquals(3, resultado.size());
        assertEquals("Ideia3", resultado.get(2).getNome());
    }

    @Test
    void testAlterarUsuarioNaoEncontrado() {
        UserEntity colaborador1 = new UserEntity(UUID.randomUUID(), "teste1", "teste1@gmail.com", "senha", Role.COLABORADOR, null, null);
        UUID idColaboradorNaoEncontrado = UUID.randomUUID();

        IdeaEntity ideia = new IdeaEntity(UUID.randomUUID(), "Ideia", "Impacto", new BigDecimal("1000.00"), "Descricao", null);

        ideia.setColaboradores(Set.of(colaborador1, new UserEntity(idColaboradorNaoEncontrado, null, null, null, null, null, null)));

        when(userRepository.findById(colaborador1.getId())).thenReturn(Optional.of(colaborador1));
        when(userRepository.findById(idColaboradorNaoEncontrado)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ideaService.postarIdeia(ideia);
        });

        assertEquals("Erro postar ideia Colaborador não encontrado com ID: " + idColaboradorNaoEncontrado, exception.getMessage());
    }

    @Test
    void testFindAllComErro() {
        when(ideaRepository.findAll()).thenThrow(new RuntimeException("Erro no banco de dados"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ideaService.findAll();
        });

        assertEquals("erro ao listar as ideias Erro no banco de dados", exception.getMessage());
    }
}