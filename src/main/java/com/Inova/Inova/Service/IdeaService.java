package com.Inova.Inova.Service;

import com.Inova.Inova.Entities.EventEntity;
import com.Inova.Inova.Entities.IdeaEntity;
import com.Inova.Inova.Entities.UserEntity;
import com.Inova.Inova.Repository.EventRepository;
import com.Inova.Inova.Repository.IdeaRepository;
import com.Inova.Inova.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
            EventEntity evento = eventRepository.findById(id_evento).orElseThrow(() -> new RuntimeException("Evento não encontrado"));

            System.out.println("Evento encontrado: " + evento.getNome());

            Set<UUID> colaboradoresIds = ideia.getColaboradores().stream().map(UserEntity::getId).collect(Collectors.toSet());

            Set<UserEntity> colaboradores = colaboradoresIds.stream().map(id -> userRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Colaborador não encontrado com ID: " + id))).collect(Collectors.toSet());

            ideia.setColaboradores(colaboradores);
            ideia.setEvento(evento);
            colaboradores.forEach(colaborador -> colaborador.setIdeia(ideia));
            evento.getIdeias().add(ideia);

            System.out.println("Colaboradores da ideia: " + colaboradores.size());

            IdeaEntity ideiaSalva = ideaRepository.save(ideia);
            eventRepository.save(evento);

            return ideiaSalva;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao postar ideia: " + e.getMessage());
        }
    }

    public List<IdeaEntity> findAll() {
        try {
            return ideaRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("erro ao listar as ideias " + e.getMessage());
        }
    }

    public void distribuirIdeias(UUID id_evento){
        try {
            EventEntity evento = eventRepository.findById(id_evento).orElseThrow(() -> new RuntimeException("Evento nao encontrado"));

            Set<UserEntity> jurados = evento.getJurados();
            Set<IdeaEntity> ideias = evento.getIdeias();

            if (jurados.isEmpty() || ideias.isEmpty()) {
                throw new RuntimeException("Não há jurados ou ideias suficientes para distribuir");
            }

            List<UserEntity> juradosList = new ArrayList<>(jurados);
            List<IdeaEntity> ideiasList = new ArrayList<>(ideias);

            Collections.shuffle(ideiasList);

            int numJurados = juradosList.size();
            for (int i = 0; i < ideiasList.size(); i++) {
                UserEntity jurado = juradosList.get(i % numJurados);
                IdeaEntity ideia = ideiasList.get(i);
                ideia.getJurados().add(jurado);
                ideaRepository.save(ideia);
            }
        }catch (Exception e){
            throw new RuntimeException("Erro ao distribuir ideias: " + e.getMessage());
        }
    }
}
