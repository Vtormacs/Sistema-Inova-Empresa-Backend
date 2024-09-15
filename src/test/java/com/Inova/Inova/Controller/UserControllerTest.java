package com.Inova.Inova.Controller;

import com.Inova.Inova.Entities.Enum.Role;
import com.Inova.Inova.Entities.UserEntity;
import com.Inova.Inova.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserControllerTest {

    @Autowired
    UserController userController;

    @MockBean
    UserRepository userRepository;

    List<UserEntity> lista = new ArrayList<>();

    @BeforeEach
    public void setup() {
        //findAll
        this.lista.add(new UserEntity(UUID.randomUUID(), "teste", "teste@gmail.com", "senha", Role.COLABORADOR, null, null, null));
        this.lista.add(new UserEntity(UUID.randomUUID(), "teste2", "teste2@gmail.com", "senha2", Role.AVALIADOR, null, null, null));
        this.lista.add(new UserEntity(UUID.randomUUID(), "teste3", "teste3@gmail.com", "senha3", Role.ADMIN, null, null, null));
        when(userRepository.findAll()).thenReturn(lista);
    }

    @Test
    void findAll() {
        var retorno = userController.findAll();

        assertEquals(HttpStatus.OK, retorno.getStatusCode());
        assertEquals(3, retorno.getBody().size());
    }

    @Test
    void testFindAllComErro() {
        when(userRepository.findAll()).thenThrow(new RuntimeException());

        var retorno = userController.findAll();

        assertEquals(HttpStatus.BAD_REQUEST, retorno.getStatusCode());
    }

    @Test
    void alterarUsuario() {
        UserEntity usuario = new UserEntity(UUID.randomUUID(), "teste", "teste@gmail.com", "senha", Role.COLABORADOR, null, null, null);
        Role novaRole = Role.ADMIN;

        when(userRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));

        var resultado = userController.alterarUsuario(novaRole, usuario.getId());

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        verify(userRepository).save(usuario);
    }

    @Test
    void testAlterarUsuarioNaoEncontrado() {
        UUID userId = UUID.randomUUID();
        Role novaRole = Role.ADMIN;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        var retorno = userController.alterarUsuario(novaRole, userId);

        assertEquals(HttpStatus.BAD_REQUEST, retorno.getStatusCode());
    }

    @Test
    void testAlterarUsuarioComRoleInvalida() {
        UUID userId = UUID.randomUUID();
        Role roleInvalida = null;  // Role nula

        when(userRepository.findById(userId)).thenReturn(Optional.of(new UserEntity()));

        var resultado = userController.alterarUsuario(roleInvalida, userId);

        assertEquals(HttpStatus.BAD_REQUEST, resultado.getStatusCode());
    }

}