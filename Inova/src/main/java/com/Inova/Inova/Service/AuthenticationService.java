package com.Inova.Inova.Service;

import com.Inova.Inova.Entities.DTO.AuthenticationDTO;
import com.Inova.Inova.Entities.DTO.RegisterDTO;
import com.Inova.Inova.Entities.Enum.Role;
import com.Inova.Inova.Entities.UserEntity;
import com.Inova.Inova.Infra.Token.TokenService;
import com.Inova.Inova.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public String login(AuthenticationDTO dado) {
        try {
            var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(dado.email(), dado.senha());
            var auth = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

            var token = tokenService.generateToken((UserEntity) auth.getPrincipal());

            return token;
        } catch (BadCredentialsException e) {
            throw e;
        }
    }

    public String registrar(RegisterDTO dado) throws Exception {
        try {
            if (userRepository.findByEmail(dado.email()) != null) {
                throw new IllegalArgumentException("Email ja esta sendo usado");
            }

            String encryptedSenha = passwordEncoder.encode(dado.senha());
            UserEntity novoUsuario = new UserEntity(dado.nome(), dado.email(), encryptedSenha, Role.COLABORADOR);

            userRepository.save(novoUsuario);

            return "Usuario registrado";
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception("erro ao registrar user", e);
        }
    }
}
