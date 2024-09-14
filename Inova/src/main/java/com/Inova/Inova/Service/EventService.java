package com.Inova.Inova.Service;

import com.Inova.Inova.Entities.EventEntity;
import com.Inova.Inova.Repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public EventEntity criarEvento(EventEntity evento) {
        try {
            return eventRepository.save(evento);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar evento " + e.getMessage());
        }
    }
}
