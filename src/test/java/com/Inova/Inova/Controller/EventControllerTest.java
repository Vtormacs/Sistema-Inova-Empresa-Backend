package com.Inova.Inova.Controller;

import com.Inova.Inova.Entities.EventEntity;
import com.Inova.Inova.Entities.UserEntity;
import com.Inova.Inova.Repository.EventRepository;
import com.Inova.Inova.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
class EventControllerTest {

    @MockBean
    EventRepository eventRepository;

    @MockBean
    UserRepository userRepository;

    @Autowired
    EventController eventController;

    @BeforeEach
    public void setup(){
        List<EventEntity> eventos = new ArrayList<>();
        eventos.add(new EventEntity(UUID.randomUUID(), "Evento 1", "Descricao", LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 5), LocalDate.of(2024, 1, 7), null,null));
        when(eventRepository.findAll()).thenReturn(eventos);
    }

    @Test
    void criarEvento() {
        EventEntity evento = new EventEntity(UUID.randomUUID(), "Evento teste", "Descricao", LocalDate.of(2024, 1, 1)
                , LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 5), LocalDate.of(2024, 1, 7), null, null);

        when(eventRepository.save(evento)).thenReturn(evento);

        var retorno = eventController.criarEvento(evento);

        assertEquals(HttpStatus.OK, retorno.getStatusCode());
    }

    @Test
    public void criarEvento_Error() {
        EventEntity evento = new EventEntity();
        evento.setNome("Evento Teste");

        when(eventRepository.save(any(EventEntity.class))).thenThrow(new RuntimeException("Erro ao salvar evento"));

        var retorno = eventController.criarEvento(evento);

        assertEquals(HttpStatus.BAD_REQUEST, retorno.getStatusCode());
    }

    @Test
    void selecaoDeJurados_Success() {
        UUID eventId = UUID.randomUUID();
        UUID jurado1Id = UUID.randomUUID();
        UUID jurado2Id = UUID.randomUUID();

        UserEntity jurado1 = new UserEntity(jurado1Id, "Jurado 1", "jurado1@gmail.com", "senha1", null, null, null, null, null);
        UserEntity jurado2 = new UserEntity(jurado2Id, "Jurado 2", "jurado2@gmail.com", "senha2", null, null, null, null, null);

        EventEntity evento = new EventEntity(eventId, "Evento Teste", "Descricao",
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 10),
                LocalDate.of(2024, 1, 5), LocalDate.of(2024, 1, 7), null, null);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(evento));
        when(userRepository.findById(jurado1Id)).thenReturn(Optional.of(jurado1));
        when(userRepository.findById(jurado2Id)).thenReturn(Optional.of(jurado2));
        when(eventRepository.save(any(EventEntity.class))).thenReturn(evento);

        List<UUID> jurados = List.of(jurado1Id, jurado2Id);

        var response = eventController.selecaoDeJurados(jurados, eventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void selecaoDeJurados_Error() {
        UUID eventId = UUID.randomUUID();
        UUID jurado1Id = UUID.randomUUID();
        UUID jurado2Id = UUID.randomUUID();

        UserEntity jurado1 = new UserEntity(jurado1Id, "Jurado 1", "jurado1@gmail.com", "senha1", null, null, null, null, null);
        UserEntity jurado2 = new UserEntity(jurado2Id, "Jurado 2", "jurado2@gmail.com", "senha2", null, null, null, null, null);

        EventEntity evento = new EventEntity(eventId, "Evento Teste", "Descricao",
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 10),
                LocalDate.of(2024, 1, 5), LocalDate.of(2024, 1, 7), null, null);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(evento));
        when(userRepository.findById(jurado1Id)).thenReturn(Optional.of(jurado1));
        when(userRepository.findById(jurado2Id)).thenReturn(Optional.of(jurado2));
        when(eventRepository.save(any(EventEntity.class))).thenThrow(new RuntimeException("Erro ao salvar jurados"));

        List<UUID> jurados = List.of(jurado1Id, jurado2Id);

        var response = eventController.selecaoDeJurados(jurados, eventId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void listarEventos_Success() {

        var response = eventController.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void listarEventos_Error() {
        when(eventRepository.findAll()).thenThrow(new RuntimeException("Erro ao listar eventos"));

        var response = eventController.findAll();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}