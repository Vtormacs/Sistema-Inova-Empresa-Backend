package com.Inova.Inova.Controller;

import com.Inova.Inova.Entities.Enum.Role;
import com.Inova.Inova.Entities.EventEntity;
import com.Inova.Inova.Entities.IdeaEntity;
import com.Inova.Inova.Entities.UserEntity;
import com.Inova.Inova.Repository.IdeaRepository;
import com.Inova.Inova.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class IdeaControllerTest {

    @MockBean
    IdeaRepository ideaRepository;

    @MockBean
    UserRepository userRepository;

    @Autowired
    IdeaController ideaController;

    @BeforeEach
    void setup() {
        List<IdeaEntity> lista = new ArrayList<>();
        lista.add(new IdeaEntity(UUID.randomUUID(), "Ideia", "Impacto", new BigDecimal("1000.00"), "Descricao", null));
        lista.add(new IdeaEntity(UUID.randomUUID(), "Ideia2", "Impacto2", new BigDecimal("1000.00"), "Descricao2", null));
        lista.add(new IdeaEntity(UUID.randomUUID(), "Ideia3", "Impacto3", new BigDecimal("1000.00"), "Descricao3", null));
        when(ideaRepository.findAll()).thenReturn(lista);
    }

    @Test
    void postarIdeia() {
        UserEntity usuario = new UserEntity(UUID.randomUUID(), "teste", "teste@gmail.com", "senha", Role.COLABORADOR, null);
        IdeaEntity ideia = new IdeaEntity(UUID.randomUUID(), "Ideia", "Impacto", new BigDecimal("1000.00"), "Descricao", usuario);

        when(userRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(ideaRepository.save(ideia)).thenReturn(ideia);

        var resultado = ideaController.postarIdeia(ideia);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals("Ideia", resultado.getBody().getNome());
        assertEquals("teste", resultado.getBody().getColaborador().getNome());
    }

    @Test
    void findAll() {
        var resultado = ideaController.findAll();

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(3, resultado.getBody().size());
    }

    @Test
    void postarIdeia_Error() {
        IdeaEntity ideia = new IdeaEntity();

        when(ideaRepository.save(any(IdeaEntity.class))).thenThrow(new RuntimeException("Erro ao postar ideia"));

        var retorno = ideaController.postarIdeia(ideia);

        assertEquals(HttpStatus.BAD_REQUEST, retorno.getStatusCode());
    }

    @Test
    void findAll_Erro(){
        when(ideaRepository.findAll()).thenThrow(new RuntimeException("Erro ao listar ideias"));

        var resultado = ideaController.findAll();

        assertEquals(HttpStatus.BAD_REQUEST, resultado.getStatusCode());
    }

}