package com.Inova.Inova.Entities;

import com.Inova.Inova.Entities.Enum.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {
    private UserEntity user;

    @BeforeEach
    public void setUp() {
        user = new UserEntity();
    }

    @Test
    public void testGetAuthoritiesForAdmin() {
        user.setRole(Role.ADMIN);

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertNotNull(authorities);
        assertEquals(3, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_COLABORADOR")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_AVALIADOR")));
    }

    @Test
    public void testGetAuthoritiesForColaborador() {
        user.setRole(Role.COLABORADOR);

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_COLABORADOR")));
    }

    @Test
    public void testGetPassword() {
        user.setSenha("password123");

        assertEquals("password123", user.getPassword());
    }

    @Test
    public void testGetUsername() {
        user.setEmail("test@example.com");

        assertEquals("test@example.com", user.getUsername());
    }

}