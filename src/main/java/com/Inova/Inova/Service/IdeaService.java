package com.Inova.Inova.Service;

import com.Inova.Inova.Entities.EventEntity;
import com.Inova.Inova.Entities.IdeaEntity;
import com.Inova.Inova.Entities.UserEntity;
import com.Inova.Inova.Repository.EventRepository;
import com.Inova.Inova.Repository.IdeaRepository;
import com.Inova.Inova.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class IdeaService {

    @Autowired
    private IdeaRepository ideaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    public IdeaEntity postarIdeia(IdeaEntity ideia, UUID id_evento) {
        try {
            System.out.println(eventRepository.findById(id_evento));
            EventEntity evento = eventRepository.findById(id_evento).orElseThrow(() -> new RuntimeException("Erro ao encontrar evento"));

            Set<UUID> colaboradoresIds = ideia.getColaboradores().stream().map(UserEntity::getId).collect(Collectors.toSet());

            Set<UserEntity> colaboradores = colaboradoresIds.stream().map(id -> {
                UserEntity colaborador = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Colaborador não encontrado com ID: " + id));

                if (colaborador.getIdeia() != null) {
                    throw new RuntimeException("Colaborador já vinculado a outra ideia: " + colaborador.getNome());
                }
                return colaborador;
            }).collect(Collectors.toSet());

            ideia.setColaboradores(colaboradores);

            colaboradores.forEach(colaborador -> colaborador.setIdeia(ideia));

            Set<IdeaEntity> ideias = evento.getIdeias();
            ideias.add(ideia);
            eventRepository.save(evento);
            return ideaRepository.save(ideia);
        } catch (Exception e) {

            System.out.println(e.getMessage());
            throw new RuntimeException("Erro postar ideia " + e.getMessage());
        }
    }

    public List<IdeaEntity> findAll() {
        try {
            return ideaRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("erro ao listar as ideias " + e.getMessage());
        }
    }
}
