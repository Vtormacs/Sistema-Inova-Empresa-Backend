package com.Inova.Inova.Service;

import com.Inova.Inova.Entities.DTO.AuthenticationDTO;
import com.Inova.Inova.Entities.DTO.RegisterDTO;
import com.Inova.Inova.Entities.UserEntity;
import com.Inova.Inova.Infra.Token.TokenService;
import com.Inova.Inova.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoginSuccess() {
        // Criando o DTO de autenticação com email e senha
        AuthenticationDTO authDTO = new AuthenticationDTO("test@example.com", "password");

        // Simulando a entidade de usuário que será retornada pela autenticação
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("test@example.com");

        // Simulando o objeto de autenticação
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userEntity);

        // Simulando a autenticação bem-sucedida
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        // Simulando a geração do token
        String expectedToken = "mocked-token";
        when(tokenService.generateToken(any(UserEntity.class))).thenReturn(expectedToken);

        // Chamando o método de login e verificando o resultado
        String resultado = authenticationService.login(authDTO);

        // Verificando se o token gerado é o esperado
        assertEquals(expectedToken, resultado);

        // Verificando se a autenticação foi chamada uma vez
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Verificando se o token foi gerado uma vez
        verify(tokenService, times(1)).generateToken(any(UserEntity.class));
    }

    @Test
    public void testLoginFailure() {
        AuthenticationDTO authDTO = new AuthenticationDTO("test@example.com", "wrongpassword");

        // Simulando a exceção `BadCredentialsException` em caso de falha de autenticação
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Verifica se a exceção `BadCredentialsException` é lançada
        assertThrows(BadCredentialsException.class, () -> {
            authenticationService.login(authDTO);
        });

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void registrar_ComSucesso() throws Exception {
        RegisterDTO dado = new RegisterDTO("teste", "teste@email.com", "password123");

        // Simulação do comportamento esperado
        when(userRepository.findByEmail(dado.email())).thenReturn(null);
        when(passwordEncoder.encode(dado.senha())).thenReturn("senha-encriptada");

        // Execução do método a ser testado
        var retorno = authenticationService.registrar(dado);

        assertEquals("Usuario registrado", retorno);

        // Verificar se o método save foi chamado com um UserEntity
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void registrar_EmailJaUsado() {
        // Dados do DTO de registro
        RegisterDTO dado = new RegisterDTO("Jane Doe", "janedoe@email.com", "password123");

        // Simulação do comportamento esperado
        when(userRepository.findByEmail(dado.email())).thenReturn(new UserEntity());

        // Verificar se a exceção IllegalArgumentException é lançada quando o email já está em uso
        assertThrows(IllegalArgumentException.class, () -> authenticationService.registrar(dado));

        // Verificar que o método save não foi chamado
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void registrar_ErroGenerico() throws Exception {
        // Dados do DTO de registro
        RegisterDTO dado = new RegisterDTO("John Doe", "johndoe@email.com", "password123");

        // Simulação do comportamento esperado
        when(userRepository.findByEmail(dado.email())).thenReturn(null);
        when(passwordEncoder.encode(dado.senha())).thenReturn("senha-encriptada");

        // Simular uma exceção ao tentar salvar o usuário
        doThrow(new RuntimeException("Erro ao salvar")).when(userRepository).save(any(UserEntity.class));

        // Verificar se a exceção genérica é lançada
        Exception exception = assertThrows(Exception.class, () -> authenticationService.registrar(dado));

        // Verificar a mensagem de erro
        assert(exception.getMessage().contains("erro ao registrar user"));

        // Verificar que o método save foi chamado uma vez
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }
}
