package com.Inova.Inova.Controller;

import com.Inova.Inova.Entities.Enum.Role;
import com.Inova.Inova.Entities.EventEntity;
import com.Inova.Inova.Entities.IdeaEntity;
import com.Inova.Inova.Entities.UserEntity;
import com.Inova.Inova.Repository.EventRepository;
import com.Inova.Inova.Repository.IdeaRepository;
import com.Inova.Inova.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class IdeaControllerTest {

    @MockBean
    IdeaRepository ideaRepository;

    @MockBean
    UserRepository userRepository;

    @Autowired
    IdeaController ideaController;

    @MockBean
    EventRepository eventRepository;


    @BeforeEach
    void setup() {
        List<IdeaEntity> lista = new ArrayList<>();
        lista.add(new IdeaEntity(UUID.randomUUID(), "Ideia", "Impacto", new BigDecimal("1000.00"), "Descricao", null, null));
        lista.add(new IdeaEntity(UUID.randomUUID(), "Ideia2", "Impacto2", new BigDecimal("1000.00"), "Descricao2", null, null));
        lista.add(new IdeaEntity(UUID.randomUUID(), "Ideia3", "Impacto3", new BigDecimal("1000.00"), "Descricao3", null, null));
        when(ideaRepository.findAll()).thenReturn(lista);
    }

    @Test
    void postarIdeia() {
        EventEntity evento = new EventEntity(UUID.randomUUID(), "Evento Teste", "Descrição do Evento", null, null, null, null, new HashSet<>(), null);
        evento.setIdeias(new HashSet<>());

        UserEntity colaborador1 = new UserEntity(UUID.randomUUID(), "teste1", "teste1@gmail.com", "senha", Role.COLABORADOR, null, null);
        UserEntity colaborador2 = new UserEntity(UUID.randomUUID(), "teste2", "teste2@gmail.com", "senha", Role.COLABORADOR, null, null);

        IdeaEntity ideia = new IdeaEntity(UUID.randomUUID(), "Ideia", "Impacto", new BigDecimal("1000.00"), "Descricao", null, null);

        when(userRepository.findById(colaborador1.getId())).thenReturn(Optional.of(colaborador1));
        when(userRepository.findById(colaborador2.getId())).thenReturn(Optional.of(colaborador2));
        when(eventRepository.findById(evento.getId())).thenReturn(Optional.of(evento));
        when(ideaRepository.save(ideia)).thenReturn(ideia);

        ideia.setColaboradores(Set.of(colaborador1, colaborador2));

        // Chamando o método do controlador
        var resultado = ideaController.postarIdeia(ideia, evento.getId());

        // Verificando o código de status e a resposta
        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals("Ideia", resultado.getBody().getNome());
    }

    @Test
    void findAll() {
        var resultado = ideaController.findAll();

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(3, resultado.getBody().size());
    }

    @Test
    void postarIdeia_Error() {
        IdeaEntity ideia = new IdeaEntity();
        EventEntity evento = new EventEntity(UUID.randomUUID(), "Evento Teste", "Descrição do Evento", null, null, null, null, new HashSet<>(), null);

        when(ideaRepository.save(any(IdeaEntity.class))).thenThrow(new RuntimeException("Erro ao postar ideia"));

        var retorno = ideaController.postarIdeia(ideia, evento.getId());

        assertEquals(HttpStatus.BAD_REQUEST, retorno.getStatusCode());
    }

    @Test
    void findAll_Erro(){
        when(ideaRepository.findAll()).thenThrow(new RuntimeException("Erro ao listar ideias"));

        var resultado = ideaController.findAll();

        assertEquals(HttpStatus.BAD_REQUEST, resultado.getStatusCode());
    }

}