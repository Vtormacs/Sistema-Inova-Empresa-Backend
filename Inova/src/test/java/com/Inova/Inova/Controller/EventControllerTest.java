package com.Inova.Inova.Controller;

import com.Inova.Inova.Entities.EventEntity;
import com.Inova.Inova.Repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
class EventControllerTest {

    @MockBean
    EventRepository eventRepository;

    @Autowired
    EventController  eventController;

    @Test
    void criarEvento() {
        EventEntity evento = new EventEntity(UUID.randomUUID(), "Evento teste", "Descricao", LocalDate.of(2024, 1, 1)
                , LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 5), LocalDate.of(2024, 1, 7));

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

        assertEquals(HttpStatus.BAD_REQUEST , retorno.getStatusCode());
    }

}