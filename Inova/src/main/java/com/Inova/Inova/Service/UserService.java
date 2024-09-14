package com.Inova.Inova.Service;

import com.Inova.Inova.Entities.Enum.Role;
import com.Inova.Inova.Entities.UserEntity;
import com.Inova.Inova.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public String alterarUsuario(Role role, UUID id) {
        try {
            UserEntity user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));

            if (role != null) {
                user.setRole(role);
                userRepository.save(user);

                return "Usuario alterado para " + role.name();
            }

            return "Role nao esta presente";
        } catch (Exception e) {
            throw new RuntimeException("erro ao alterar usuario " + e.getMessage());
        }
    }

    public List<UserEntity> findAll() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("erro ao listar usuarios " + e.getMessage());
        }
    }

}
