package com.Inova.Inova.Infra.Token;

import com.Inova.Inova.Entities.UserEntity;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Atribui o valor da variável secreto usando ReflectionTestUtils
        ReflectionTestUtils.setField(tokenService, "secreto", "my-secret-key");
    }

    @Test
    void testGenerateToken_Success() {
        when(userEntity.getEmail()).thenReturn("user@example.com");

        String token = tokenService.generateToken(userEntity);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testGenerateToken_Exception() {
        // Para forçar a exceção, podemos definir um valor inválido no segredo
        ReflectionTestUtils.setField(tokenService, "secreto", "");

        assertThrows(RuntimeException.class, () -> {
            tokenService.generateToken(userEntity);
        });
    }

    @Test
    void testValidarToken_Success() {
        when(userEntity.getEmail()).thenReturn("user@example.com");

        String token = tokenService.generateToken(userEntity);

        String subject = tokenService.validarToken(token);

        assertEquals("user@example.com", subject);
    }

    @Test
    void testValidarToken_InvalidToken() {
        String invalidToken = "invalid.token.value";

        String result = tokenService.validarToken(invalidToken);

        assertNull(result);
    }

    @Test
    void testGenerateExpirationDate() {
        Instant expirationDate = LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));

        Instant generatedExpirationDate = ReflectionTestUtils.invokeMethod(tokenService, "generateeExpirationDate");

        assertEquals(expirationDate.getEpochSecond(), generatedExpirationDate.getEpochSecond(), 1);
    }
}
