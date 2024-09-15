package com.Inova.Inova.Controller;

import com.Inova.Inova.Entities.DTO.AuthenticationDTO;
import com.Inova.Inova.Entities.DTO.RegisterDTO;
import com.Inova.Inova.Service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogin_Sucesso() {
        // Criando o DTO de autenticação com email e senha
        AuthenticationDTO authDTO = new AuthenticationDTO("test@example.com", "password");

        // Simulando o retorno esperado do serviço de autenticação
        String expectedToken = "mocked-token";
        when(authenticationService.login(authDTO)).thenReturn(expectedToken);

        // Chamando o método de login do controller
        ResponseEntity<?> response = authenticationController.login(authDTO);

        // Verificando o código de status e o token no corpo da resposta
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testLogin_Falha() {
        // Criando o DTO de autenticação com email e senha incorretos
        AuthenticationDTO authDTO = new AuthenticationDTO("test@example.com", "wrongpassword");

        // Simulando uma falha de autenticação (BadCredentialsException)
        when(authenticationService.login(authDTO)).thenThrow(new BadCredentialsException("Invalid credentials"));

        // Chamando o método de login do controller e verificando o código de status
        ResponseEntity<?> response = authenticationController.login(authDTO);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

        // Verificando se a autenticação falhou e o serviço foi chamado uma vez
        verify(authenticationService, times(1)).login(authDTO);
    }

    @Test
    public void testRegistrar_Sucesso() throws Exception {
        // Creating the registration DTO
        RegisterDTO registerDTO = new RegisterDTO("Jane Doe", "janedoe@example.com", "password123");

        // Simulating the expected behavior of the service
        when(authenticationService.registrar(registerDTO)).thenReturn("Usuario registrado");

        // Calling the registration method of the controller
        ResponseEntity<?> response = authenticationController.registrar(registerDTO);

        // Verifying the status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testRegistrar_EmailJaUsado() throws Exception {
        // Criando o DTO de registro
        RegisterDTO registerDTO = new RegisterDTO("John Doe", "johndoe@example.com", "password123");

        // Simulando um erro de email já usado (IllegalArgumentException)
        doThrow(new IllegalArgumentException("Email já em uso")).when(authenticationService).registrar(registerDTO);

        // Chamando o método de registro e verificando o código de status
        ResponseEntity<?> response = authenticationController.registrar(registerDTO);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email já em uso", response.getBody());

        // Verificando se o método registrar foi chamado
        verify(authenticationService, times(1)).registrar(registerDTO);
    }

    @Test
    public void testRegistrar_ErroGenerico() throws Exception {
        // Criando o DTO de registro
        RegisterDTO registerDTO = new RegisterDTO("John Doe", "johndoe@example.com", "password123");

        // Simulando uma exceção genérica (RuntimeException)
        doThrow(new RuntimeException("Erro ao processar o registro")).when(authenticationService).registrar(registerDTO);

        // Chamando o método de registro e verificando o código de status
        ResponseEntity<?> response = authenticationController.registrar(registerDTO);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("erro ao processar o registro.", response.getBody());

        // Verificando se o método registrar foi chamado
        verify(authenticationService, times(1)).registrar(registerDTO);
    }
}
