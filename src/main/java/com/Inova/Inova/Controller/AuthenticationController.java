package com.Inova.Inova.Controller;

import com.Inova.Inova.Entities.DTO.AuthenticationDTO;
import com.Inova.Inova.Entities.DTO.LoginResponseDTO;
import com.Inova.Inova.Entities.DTO.RegisterDTO;
import com.Inova.Inova.Service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthenticationDTO dado) {
        try {
            var token = authenticationService.login(dado);
            return ResponseEntity.ok(new LoginResponseDTO(token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity registrar(@RequestBody RegisterDTO dado) {
        try {
            authenticationService.registrar(dado);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("erro ao processar o registro.");
        }
    }
}
