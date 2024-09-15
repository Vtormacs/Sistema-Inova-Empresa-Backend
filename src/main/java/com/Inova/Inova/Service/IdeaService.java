package com.Inova.Inova.Service;

import com.Inova.Inova.Entities.IdeaEntity;
import com.Inova.Inova.Entities.UserEntity;
import com.Inova.Inova.Repository.IdeaRepository;
import com.Inova.Inova.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IdeaService {

    @Autowired
    private IdeaRepository ideaRepository;

    @Autowired
    private UserRepository userRepository;

    public IdeaEntity postarIdeia(IdeaEntity ideia) {
        try {
            UserEntity user = userRepository.findById(ideia.getColaborador().getId()).orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));

            user.setIdeia(ideia);

            ideia.setColaborador(user);

            return ideaRepository.save(ideia);
        } catch (Exception e) {
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
