package com.Inova.Inova.Service;

import com.Inova.Inova.Entities.UserEntity;
import com.Inova.Inova.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthorizationServiceTest {

    @MockBean
    private UserRepository userRepository;

    @InjectMocks
    private AuthorizationService authorizationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoadUserByUsername_UserFound() {
        String email = "test@example.com";
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setSenha("password123");

        when(userRepository.findByEmail(email)).thenReturn(user);

        UserDetails resultado = authorizationService.loadUserByUsername(email);

        assertNotNull(resultado);
        assertEquals(email, resultado.getUsername());
        assertEquals("password123", resultado.getPassword());

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        String email = "notfound@example.com";

        when(userRepository.findByEmail(email)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            authorizationService.loadUserByUsername(email);
        });

        verify(userRepository, times(1)).findByEmail(email);
    }
}
