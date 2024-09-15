package com.Inova.Inova.Service;

import com.Inova.Inova.Entities.Enum.Role;
import com.Inova.Inova.Entities.EventEntity;
import com.Inova.Inova.Entities.UserEntity;
import com.Inova.Inova.Repository.EventRepository;
import com.Inova.Inova.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    public EventEntity criarEvento(EventEntity evento) {
        try {
            return eventRepository.save(evento);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar evento " + e.getMessage());
        }
    }

    public EventEntity selecaoDeJurados(List<UUID> ids, UUID idDoEvento) {
        try {
            EventEntity eventoSelecionado = eventRepository.findById(idDoEvento).orElseThrow(() -> new RuntimeException("Evento não encontrado"));

            Set<UserEntity> jurados = ids.stream().map(id -> {
                UserEntity jurado = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Jurado com ID " + id + " não encontrado"));
                jurado.setRole(Role.AVALIADOR);
                return jurado;
            }).collect(Collectors.toSet());

            eventoSelecionado.setJurados(jurados);

            EventEntity eventoAtualizado = eventRepository.save(eventoSelecionado);

            return eventoAtualizado;
        } catch (Exception e) {
            System.out.println("Erro ao selecionar jurados: " + e.getMessage());
            throw new RuntimeException("Erro ao selecionar jurados: " + e.getMessage());
        }
    }

    public List<EventEntity> findAll() {
        try {
            return eventRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar eventos " + e.getMessage());
        }
    }
}
