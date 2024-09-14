package com.Inova.Inova.Controller;

import com.Inova.Inova.Entities.Enum.Role;
import com.Inova.Inova.Entities.UserEntity;
import com.Inova.Inova.Service.UserService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/listar-usuarios")
    public ResponseEntity<List<UserEntity>> findAll() {
        try {
            return ResponseEntity.ok(userService.findAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/alterar-usuario")
    public ResponseEntity<String> update(@RequestBody Role role, @RequestParam UUID uuid) {
        try {
            return ResponseEntity.ok(userService.alterarUsuario(role, uuid));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

//    @DeleteMapping("/delete")
//    public ResponseEntity<String> delete(@RequestParam UUID uuid) {
//        try {
//            return ResponseEntity.ok(userService.delete(uuid));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }

}
