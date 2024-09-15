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
import static org.mockito.Mockito.*;

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

    UUID idEvento;
    EventEntity evento;
    UserEntity jurado1;
    UserEntity jurado2;
    Set<UserEntity> jurados;
    IdeaEntity ideia1;
    IdeaEntity ideia2;
    Set<IdeaEntity> ideias;

    @BeforeEach
    void setup() {
        idEvento = UUID.randomUUID();

        jurado1 = new UserEntity(UUID.randomUUID(), "Jurado 1", "jurado1@gmail.com", "senha", Role.AVALIADOR, null, null, null);
        jurado2 = new UserEntity(UUID.randomUUID(), "Jurado 2", "jurado2@gmail.com", "senha", Role.AVALIADOR, null, null, null);

        jurados = new HashSet<>(Arrays.asList(jurado1, jurado2));

        ideia1 = new IdeaEntity(UUID.randomUUID(), "Ideia 1", "Impacto", new BigDecimal("1000.00"), "Descrição 1", null, null, null);
        ideia1.setJurados(new HashSet<>());

        ideia2 = new IdeaEntity(UUID.randomUUID(), "Ideia 2", "Impacto 2", new BigDecimal("2000.00"), "Descrição 2", null, null, null);
        ideia2.setJurados(new HashSet<>());

        ideias = new HashSet<>(Arrays.asList(ideia1, ideia2));

        evento = new EventEntity(idEvento, "Evento Teste", "Descrição do Evento", null, null, null, null, new HashSet<>(), null);
        evento.setJurados(jurados);
        evento.setIdeias(ideias);
    }

    @Test
    void postarIdeia() {
        EventEntity evento = new EventEntity(UUID.randomUUID(), "Evento Teste", "Descrição do Evento", null, null, null, null, new HashSet<>(), null);
        evento.setIdeias(new HashSet<>());

        UserEntity colaborador1 = new UserEntity(UUID.randomUUID(), "teste1", "teste1@gmail.com", "senha", Role.COLABORADOR, null, null, null);
        UserEntity colaborador2 = new UserEntity(UUID.randomUUID(), "teste2", "teste2@gmail.com", "senha", Role.COLABORADOR, null, null, null);

        IdeaEntity ideia = new IdeaEntity(UUID.randomUUID(), "Ideia", "Impacto", new BigDecimal("1000.00"), "Descricao", null, null, null);

        when(userRepository.findById(colaborador1.getId())).thenReturn(Optional.of(colaborador1));
        when(userRepository.findById(colaborador2.getId())).thenReturn(Optional.of(colaborador2));
        when(eventRepository.findById(evento.getId())).thenReturn(Optional.of(evento));
        when(ideaRepository.save(ideia)).thenReturn(ideia);

        ideia.setColaboradores(Set.of(colaborador1, colaborador2));

        var resultado = ideaController.postarIdeia(ideia, evento.getId());

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals("Ideia", resultado.getBody().getNome());
    }

    @Test
    void findAll() {
        when(ideaRepository.findAll()).thenReturn(List.of(ideia1, ideia2));

        var resultado = ideaController.findAll();

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(2, resultado.getBody().size());
    }

    @Test
    void postarIdeia_Error() {
        when(ideaRepository.save(any(IdeaEntity.class))).thenThrow(new RuntimeException("Erro ao postar ideia"));

        var retorno = ideaController.postarIdeia(ideia1, evento.getId());

        assertEquals(HttpStatus.BAD_REQUEST, retorno.getStatusCode());
    }

    @Test
    void findAll_Erro(){
        when(ideaRepository.findAll()).thenThrow(new RuntimeException("Erro ao listar ideias"));

        var resultado = ideaController.findAll();

        assertEquals(HttpStatus.BAD_REQUEST, resultado.getStatusCode());
    }

    @Test
    void testDistribuirIdeiasSucesso() {
        when(eventRepository.findById(idEvento)).thenReturn(Optional.of(evento));

        ideaController.distribuirIdeias(idEvento);

        assertTrue(ideia1.getJurados().contains(jurado1) || ideia1.getJurados().contains(jurado2));
        assertTrue(ideia2.getJurados().contains(jurado1) || ideia2.getJurados().contains(jurado2));

        verify(ideaRepository, times(2)).save(any(IdeaEntity.class));
    }

    @Test
    void testDistribuirIdeiasSemIdeias() {
        evento.setIdeias(new HashSet<>()); // Evento sem ideias
        when(eventRepository.findById(idEvento)).thenReturn(Optional.of(evento));

        var response = ideaController.distribuirIdeias(idEvento);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testDistribuirIdeiasEventoNaoEncontrado() {
        when(eventRepository.findById(idEvento)).thenReturn(Optional.empty());

        var response = ideaController.distribuirIdeias(idEvento);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
