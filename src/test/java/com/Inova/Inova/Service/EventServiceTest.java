package com.Inova.Inova.Service;

import com.Inova.Inova.Entities.EventEntity;
import com.Inova.Inova.Repository.EventRepository;
import com.Inova.Inova.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class EventServiceTest {

    @MockBean
    EventRepository eventRepository;

    @Autowired
    EventService eventService;

    @Test
    void criarEvento() {
        EventEntity evento = new EventEntity(UUID.randomUUID(), "Evento teste", "Descricao", LocalDate.of(2024, 1, 1)
                , LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 5), LocalDate.of(2024, 1, 7));

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


}