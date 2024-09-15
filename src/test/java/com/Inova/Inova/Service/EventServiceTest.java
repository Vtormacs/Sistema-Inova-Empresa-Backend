package com.Inova.Inova.Service;

import com.Inova.Inova.Entities.EventEntity;
import com.Inova.Inova.Entities.UserEntity;
import com.Inova.Inova.Repository.EventRepository;
import com.Inova.Inova.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class EventServiceTest {

    @MockBean
    EventRepository eventRepository;

    @MockBean
    UserRepository userRepository;

    @Autowired
    EventService eventService;

    @Test
    void criarEvento() {
        EventEntity evento = new EventEntity(UUID.randomUUID(), "Evento teste", "Descricao", LocalDate.of(2024, 1, 1)
                , LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 5), LocalDate.of(2024, 1, 7), null, null);

        when(eventRepository.save(evento)).thenReturn(evento);

        var retorno = eventService.criarEvento(evento);

        assertEquals("Evento teste", retorno.getNome());
        assertNotNull(retorno);
        verify(eventRepository, times(1)).save(evento);
    }

    @Test
    public void criarEvento_Error() {
        EventEntity evento = new EventEntity();
        evento.setNome("Evento Teste");

        when(eventRepository.save(any(EventEntity.class))).thenThrow(new RuntimeException("Erro ao salvar evento"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            eventService.criarEvento(evento);
        });
        assertEquals("Erro ao criar evento Erro ao salvar evento", thrown.getMessage());
    }

    @Test
    void selecaoDeJurados() {
        UUID eventId = UUID.randomUUID();
        UUID jurado1Id = UUID.randomUUID();
        UUID jurado2Id = UUID.randomUUID();

        EventEntity evento = new EventEntity(eventId, "Evento Teste", "Descricao", LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 5), LocalDate.of(2024, 1, 7), null, null);

        UserEntity jurado1 = new UserEntity(jurado1Id, "Jurado 1", "jurado1@gmail.com", "senha1", null, null, null);
        UserEntity jurado2 = new UserEntity(jurado2Id, "Jurado 2", "jurado2@gmail.com", "senha2", null, null, null);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(evento));
        when(userRepository.findById(jurado1Id)).thenReturn(Optional.of(jurado1));
        when(userRepository.findById(jurado2Id)).thenReturn(Optional.of(jurado2));
        when(eventRepository.save(evento)).thenReturn(evento);

        var eventoAtualizado = eventService.selecaoDeJurados(List.of(jurado1Id, jurado2Id), eventId);

        assertNotNull(eventoAtualizado);
        assertEquals(2, eventoAtualizado.getJurados().size());
        assertTrue(eventoAtualizado.getJurados().contains(jurado1));
        assertTrue(eventoAtualizado.getJurados().contains(jurado2));

    }

    @Test
    void selecaoDeJurados_ErrorEventoNaoEncontrado() {
        UUID eventId = UUID.randomUUID();
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            eventService.selecaoDeJurados(List.of(UUID.randomUUID()), eventId);
        });

        assertEquals("Erro ao selecionar jurados: Evento não encontrado", thrown.getMessage());
    }

    @Test
    void selecaoDeJurados_ErrorJuradoNaoEncontrado() {
        UUID eventId = UUID.randomUUID();
        UUID jurado1Id = UUID.randomUUID();

        EventEntity evento = new EventEntity(eventId, "Evento Teste", "Descricao", LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 5), LocalDate.of(2024, 1, 7), null, null);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(evento));
        when(userRepository.findById(jurado1Id)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            eventService.selecaoDeJurados(List.of(jurado1Id), eventId);
        });

        assertEquals("Erro ao selecionar jurados: Jurado com ID " + jurado1Id + " não encontrado", thrown.getMessage());
    }

    @Test
    void findAll() {
        List<EventEntity> eventos = List.of(
                new EventEntity(UUID.randomUUID(), "Evento 1", "Descrição 1", LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 5), LocalDate.of(2024, 1, 7), null, null),
                new EventEntity(UUID.randomUUID(), "Evento 2", "Descrição 2", LocalDate.of(2024, 2, 1),
                        LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 5), LocalDate.of(2024, 2, 7), null, null)
        );

        when(eventRepository.findAll()).thenReturn(eventos);

        var retorno = eventService.findAll();

        assertEquals(2, retorno.size());
        assertEquals("Evento 1", retorno.get(0).getNome());
        assertEquals("Evento 2", retorno.get(1).getNome());

        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void findAll_Error() {
        when(eventRepository.findAll()).thenThrow(new RuntimeException("Erro ao listar eventos"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eventService.findAll();
        });

        assertEquals("Erro ao listar eventos Erro ao listar eventos", exception.getMessage());
    }
}