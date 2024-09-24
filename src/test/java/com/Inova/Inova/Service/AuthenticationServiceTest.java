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
        AuthenticationDTO authDTO = new AuthenticationDTO("test@example.com", "password");

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("test@example.com");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userEntity);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        String expectedToken = "mocked-token";
        when(tokenService.generateToken(any(UserEntity.class))).thenReturn(expectedToken);

        String resultado = authenticationService.login(authDTO);

        assertEquals(expectedToken, resultado);

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));

        verify(tokenService, times(1)).generateToken(any(UserEntity.class));
    }

    @Test
    public void testLoginFailure() {
        AuthenticationDTO authDTO = new AuthenticationDTO("test@example.com", "wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> {
            authenticationService.login(authDTO);
        });

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void registrar_ComSucesso() throws Exception {
        RegisterDTO dado = new RegisterDTO("teste", "teste@email.com", "password123");

        when(userRepository.findByEmail(dado.email())).thenReturn(null);
        when(passwordEncoder.encode(dado.senha())).thenReturn("senha-encriptada");

        var retorno = authenticationService.registrar(dado);

        assertEquals("Usuario registrado", retorno);

        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void registrar_EmailJaUsado() {
        RegisterDTO dado = new RegisterDTO("Jane Doe", "janedoe@email.com", "password123");

        when(userRepository.findByEmail(dado.email())).thenReturn(new UserEntity());

        assertThrows(IllegalArgumentException.class, () -> authenticationService.registrar(dado));

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void registrar_ErroGenerico() throws Exception {
        RegisterDTO dado = new RegisterDTO("John Doe", "johndoe@email.com", "password123");

        when(userRepository.findByEmail(dado.email())).thenReturn(null);
        when(passwordEncoder.encode(dado.senha())).thenReturn("senha-encriptada");

        doThrow(new RuntimeException("Erro ao salvar")).when(userRepository).save(any(UserEntity.class));

        Exception exception = assertThrows(Exception.class, () -> authenticationService.registrar(dado));

        assert(exception.getMessage().contains("erro ao registrar user"));

        verify(userRepository, times(1)).save(any(UserEntity.class));
    }
}
