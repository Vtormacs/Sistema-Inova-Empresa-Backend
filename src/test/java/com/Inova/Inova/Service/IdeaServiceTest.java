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

    @MockBean
    EventRepository eventRepository;

    @Autowired
    IdeaService ideaService;

    private UserEntity colaborador1;
    UserEntity colaborador2;
    EventEntity evento;
    IdeaEntity ideia1;
    IdeaEntity ideia2;
    UserEntity jurado;
    IdeaEntity ideia;

    @BeforeEach
    void setup() {
        colaborador1 = new UserEntity(UUID.randomUUID(), "teste1", "teste1@gmail.com", "senha", Role.COLABORADOR, null, null, null);
        colaborador2 = new UserEntity(UUID.randomUUID(), "teste2", "teste2@gmail.com", "senha", Role.COLABORADOR, null, null, null);

        evento = new EventEntity(UUID.randomUUID(), "Evento Teste", "Descrição do Evento", null, null, null, null, new HashSet<>(), null);

        ideia1 = new IdeaEntity(UUID.randomUUID(), "Ideia 1", "Impacto 1", new BigDecimal("1000.00"), "Descrição 1", null, null, null, null);
        ideia2 = new IdeaEntity(UUID.randomUUID(), "Ideia 2", "Impacto 2", new BigDecimal("2000.00"), "Descrição 2", null, null, null, null);

        when(ideaRepository.findAll()).thenReturn(List.of(ideia1, ideia2));

        jurado = new UserEntity(UUID.randomUUID(), "Jurado", "jurado@gmail.com", "senha", Role.COLABORADOR, null, null, null);
        ideia = new IdeaEntity(UUID.randomUUID(), "Ideia", "Impacto", new BigDecimal("1000.00"), "Descrição", null, null, null, new HashMap<>());

        ideia.setJurados(Set.of(jurado));
        jurado.setAvaliacoes(new HashSet<>());

        when(ideaRepository.findById(ideia.getId())).thenReturn(Optional.of(ideia));
        when(userRepository.findById(jurado.getId())).thenReturn(Optional.of(jurado));
    }

    @Test
    void postarIdeia() {
        EventEntity evento = new EventEntity(UUID.randomUUID(), "Evento Teste", "Descrição do Evento", null, null, null, null, new HashSet<>(), null);
        evento.setIdeias(new HashSet<>());

        UserEntity colaborador1 = new UserEntity(UUID.randomUUID(), "teste1", "teste1@gmail.com", "senha", Role.COLABORADOR, null, null, null);
        UserEntity colaborador2 = new UserEntity(UUID.randomUUID(), "teste2", "teste2@gmail.com", "senha", Role.COLABORADOR, null, null, null);

        IdeaEntity ideia = new IdeaEntity(UUID.randomUUID(), "Ideia", "Impacto", new BigDecimal("1000.00"), "Descricao", null, null, null, null);

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
        assertEquals(2, resultado.size());
        assertEquals("Ideia 2", resultado.get(1).getNome());
    }

    @Test
    void testAlterarUsuarioNaoEncontrado() {
        UUID idColaboradorNaoEncontrado = UUID.randomUUID();
        ideia1.setColaboradores(Set.of(colaborador1, new UserEntity(idColaboradorNaoEncontrado, null, null, null, null, null, null, null)));

        when(userRepository.findById(colaborador1.getId())).thenReturn(Optional.of(colaborador1));
        when(userRepository.findById(idColaboradorNaoEncontrado)).thenReturn(Optional.empty());
        when(eventRepository.findById(evento.getId())).thenReturn(Optional.of(evento));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> ideaService.postarIdeia(ideia1, evento.getId()));

        assertEquals("Erro ao postar ideia: Colaborador não encontrado com ID: " + idColaboradorNaoEncontrado, exception.getMessage());
    }

    @Test
    void testFindAllComErro() {
        when(ideaRepository.findAll()).thenThrow(new RuntimeException("Erro no banco de dados"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> ideaService.findAll());

        assertEquals("erro ao listar as ideias Erro no banco de dados", exception.getMessage());
    }

    @Test
    void testDistribuirIdeiasSucesso() {
        UUID idEvento = UUID.randomUUID();

        EventEntity evento = new EventEntity(idEvento, "Evento Teste", "Descrição do Evento", null, null, null, null, new HashSet<>(), null);

        UserEntity jurado1 = new UserEntity(UUID.randomUUID(), "Jurado 1", "jurado1@gmail.com", "senha", Role.COLABORADOR, null, null, null);
        UserEntity jurado2 = new UserEntity(UUID.randomUUID(), "Jurado 2", "jurado2@gmail.com", "senha", Role.COLABORADOR, null, null, null);

        Set<UserEntity> jurados = new HashSet<>(Arrays.asList(jurado1, jurado2));

        IdeaEntity ideia1 = new IdeaEntity(UUID.randomUUID(), "Ideia 1", "Impacto", new BigDecimal("1000.00"), "Descrição 1", null, null, null, null);
        ideia1.setJurados(new HashSet<>()); // Inicializando o conjunto de jurados

        IdeaEntity ideia2 = new IdeaEntity(UUID.randomUUID(), "Ideia 2", "Impacto 2", new BigDecimal("2000.00"), "Descrição 2", null, null, null, null);
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
        UUID idEvento = evento.getId();
        evento.setJurados(Set.of(colaborador1));
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
    @Test
    void avaliarIdeiaComNotaValida() {
        int nota = 5;
        ideaService.avaliarIdeia(ideia.getId(), jurado.getId(), nota);

        assertEquals(nota, ideia.getNotasJurados().get(jurado.getId()));
        assertTrue(jurado.getAvaliacoes().contains(ideia));

        verify(ideaRepository, times(1)).save(ideia);
        verify(userRepository, times(1)).save(jurado);
    }

    @Test
    void avaliarIdeiaComNotaInvalida() {
        int nota = 2; // Nota abaixo do mínimo permitido

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ideaService.avaliarIdeia(ideia.getId(), jurado.getId(), nota);
        });

        assertEquals("A nota deve ser entre 3 e 10.", exception.getMessage());
        verify(ideaRepository, never()).save(any(IdeaEntity.class));
        verify(userRepository, never()).save(any(UserEntity.class));
    }


    @Test
    void avaliarIdeiaComIdeiaNaoEncontrada() {
        UUID idInvalido = UUID.randomUUID();

        when(ideaRepository.findById(idInvalido)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ideaService.avaliarIdeia(idInvalido, jurado.getId(), 5);
        });

        assertEquals("Ideia não encontrada", exception.getMessage());

        verify(ideaRepository, never()).save(any(IdeaEntity.class));
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void avaliarIdeiaComJuradoNaoEncontrado() {
        UUID idJuradoInvalido = UUID.randomUUID();

        when(userRepository.findById(idJuradoInvalido)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ideaService.avaliarIdeia(ideia.getId(), idJuradoInvalido, 5);
        });

        assertEquals("Jurado não encontrado", exception.getMessage());

        verify(ideaRepository, never()).save(any(IdeaEntity.class));
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void avaliarIdeiaJuradoSemPermissao() {
        UserEntity juradoSemPermissao = new UserEntity(UUID.randomUUID(), "Jurado Sem Permissao", "jurado2@gmail.com", "senha", Role.COLABORADOR, null, null, null);

        when(userRepository.findById(juradoSemPermissao.getId())).thenReturn(Optional.of(juradoSemPermissao));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ideaService.avaliarIdeia(ideia.getId(), juradoSemPermissao.getId(), 5);
        });

        assertEquals("Este jurado não tem permissão para avaliar esta ideia", exception.getMessage());

        verify(ideaRepository, never()).save(any(IdeaEntity.class));
        verify(userRepository, never()).save(any(UserEntity.class));
    }
}
