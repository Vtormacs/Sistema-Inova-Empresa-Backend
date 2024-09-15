package com.Inova.Inova.Service;

import com.Inova.Inova.Entities.Enum.Role;
import com.Inova.Inova.Entities.EventEntity;
import com.Inova.Inova.Entities.IdeaEntity;
import com.Inova.Inova.Entities.UserEntity;
import com.Inova.Inova.Repository.EventRepository;
import com.Inova.Inova.Repository.IdeaRepository;
import com.Inova.Inova.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class IdeaServiceTest {

    @MockBean
    IdeaRepository ideaRepository;

    @MockBean
    UserRepository userRepository;

    @Autowired
    IdeaService ideaService;

    @MockBean
    EventRepository eventRepository;

    @BeforeEach
    void setup() {
        List<IdeaEntity> lista = new ArrayList<>();
        lista.add(new IdeaEntity(UUID.randomUUID(), "Ideia", "Impacto", new BigDecimal("1000.00"), "Descricao", null, null, null));
        lista.add(new IdeaEntity(UUID.randomUUID(), "Ideia2", "Impacto2", new BigDecimal("1000.00"), "Descricao2", null, null, null));
        lista.add(new IdeaEntity(UUID.randomUUID(), "Ideia3", "Impacto3", new BigDecimal("1000.00"), "Descricao3", null, null, null));
        when(ideaRepository.findAll()).thenReturn(lista);
    }

    @Test
    void postarIdeia() {
        EventEntity evento = new EventEntity(UUID.randomUUID(), "Evento Teste", "Descrição do Evento", null, null, null, null, new HashSet<>(), null);
        evento.setIdeias(new HashSet<>());

        UserEntity colaborador1 = new UserEntity(UUID.randomUUID(), "teste1", "teste1@gmail.com", "senha", Role.COLABORADOR, null, null, null);
        UserEntity colaborador2 = new UserEntity(UUID.randomUUID(), "teste2", "teste2@gmail.com", "senha", Role.COLABORADOR, null, null, null);

        IdeaEntity ideia = new IdeaEntity(UUID.randomUUID(), "Ideia", "Impacto", new BigDecimal("1000.00"), "Descricao", null, null, null);

        when(userRepository.findById(colaborador1.getId())).thenReturn(Optional.of(colaborador1));
        when(userRepository.findById(colaborador2.getId())).thenReturn(Optional.of(colaborador2));
        when(eventRepository.findById(evento.getId())).thenReturn(Optional.of(evento));
        when(ideaRepository.save(ideia)).thenReturn(ideia);

        ideia.setColaboradores(Set.of(colaborador1, colaborador2));

        var resultado = ideaService.postarIdeia(ideia, evento.getId());

        assertEquals("Ideia", resultado.getNome());
        assertEquals(2, resultado.getColaboradores().size());
        assertEquals(evento.getId(), resultado.getEvento().getId());
    }

    @Test
    void findAll() {
        var resultado = ideaService.findAll();

        assertEquals(3, resultado.size());
        assertEquals("Ideia3", resultado.get(2).getNome());
    }

    @Test
    void testAlterarUsuarioNaoEncontrado() {
        UserEntity colaborador1 = new UserEntity(UUID.randomUUID(), "teste1", "teste1@gmail.com", "senha", Role.COLABORADOR, null, null, null);
        UUID idColaboradorNaoEncontrado = UUID.randomUUID();
        EventEntity evento = new EventEntity(UUID.randomUUID(), "Evento Teste", "Descrição do Evento", null, null, null, null, new HashSet<>(), null);

        IdeaEntity ideia = new IdeaEntity(UUID.randomUUID(), "Ideia", "Impacto", new BigDecimal("1000.00"), "Descricao", null, null, null);
        ideia.setColaboradores(Set.of(colaborador1, new UserEntity(idColaboradorNaoEncontrado, null, null, null, null, null, null, null)));

        when(userRepository.findById(colaborador1.getId())).thenReturn(Optional.of(colaborador1));
        when(userRepository.findById(idColaboradorNaoEncontrado)).thenReturn(Optional.empty());
        when(eventRepository.findById(evento.getId())).thenReturn(Optional.of(evento));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ideaService.postarIdeia(ideia, evento.getId());
        });

        assertEquals("Erro ao postar ideia: Colaborador não encontrado com ID: " + idColaboradorNaoEncontrado, exception.getMessage());
    }

    @Test
    void testFindAllComErro() {
        when(ideaRepository.findAll()).thenThrow(new RuntimeException("Erro no banco de dados"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ideaService.findAll();
        });

        assertEquals("erro ao listar as ideias Erro no banco de dados", exception.getMessage());
    }

    @Test
    void testDistribuirIdeiasSucesso() {
        UUID idEvento = UUID.randomUUID();

        EventEntity evento = new EventEntity(idEvento, "Evento Teste", "Descrição do Evento", null, null, null, null, new HashSet<>(), null);

        UserEntity jurado1 = new UserEntity(UUID.randomUUID(), "Jurado 1", "jurado1@gmail.com", "senha", Role.COLABORADOR, null, null, null);
        UserEntity jurado2 = new UserEntity(UUID.randomUUID(), "Jurado 2", "jurado2@gmail.com", "senha", Role.COLABORADOR, null, null, null);

        Set<UserEntity> jurados = new HashSet<>(Arrays.asList(jurado1, jurado2));

        IdeaEntity ideia1 = new IdeaEntity(UUID.randomUUID(), "Ideia 1", "Impacto", new BigDecimal("1000.00"), "Descrição 1", null, null, null);
        ideia1.setJurados(new HashSet<>()); // Inicializando o conjunto de jurados

        IdeaEntity ideia2 = new IdeaEntity(UUID.randomUUID(), "Ideia 2", "Impacto 2", new BigDecimal("2000.00"), "Descrição 2", null, null, null);
        ideia2.setJurados(new HashSet<>()); // Inicializando o conjunto de jurados

        Set<IdeaEntity> ideias = new HashSet<>(Arrays.asList(ideia1, ideia2));

        evento.setJurados(jurados);
        evento.setIdeias(ideias);

        when(eventRepository.findById(idEvento)).thenReturn(Optional.of(evento));

        ideaService.distribuirIdeias(idEvento);

        assertTrue(ideia1.getJurados().contains(jurado1) || ideia1.getJurados().contains(jurado2));
        assertTrue(ideia2.getJurados().contains(jurado1) || ideia2.getJurados().contains(jurado2));

        verify(ideaRepository, times(2)).save(any(IdeaEntity.class));
    }

    @Test
    void testDistribuirIdeiasSemIdeias() {
        UUID idEvento = UUID.randomUUID();

        EventEntity evento = new EventEntity(idEvento, "Evento Teste", "Descrição do Evento", null, null, null, null, new HashSet<>(), null);
        evento.setJurados(new HashSet<>(Arrays.asList(
                new UserEntity(UUID.randomUUID(), "Jurado 1", "jurado1@gmail.com", "senha", Role.AVALIADOR, null, null, null)
        )));
        evento.setIdeias(new HashSet<>());

        when(eventRepository.findById(idEvento)).thenReturn(Optional.of(evento));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ideaService.distribuirIdeias(idEvento);
        });

        assertEquals("Erro ao distribuir ideias: Não há jurados ou ideias suficientes para distribuir", exception.getMessage());
    }

    @Test
    void testDistribuirIdeiasEventoNaoEncontrado() {
        UUID idEvento = UUID.randomUUID();

        when(eventRepository.findById(idEvento)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ideaService.distribuirIdeias(idEvento);
        });

        assertEquals("Erro ao distribuir ideias: Evento nao encontrado", exception.getMessage());
    }
}