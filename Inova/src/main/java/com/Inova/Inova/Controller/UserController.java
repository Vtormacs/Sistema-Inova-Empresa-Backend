package com.Inova.Inova.Controller;

import com.Inova.Inova.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

//    @PostMapping("/save")
//    public ResponseEntity<UserEntity> save(@RequestBody UserEntity user) {
//        try {
//            return ResponseEntity.ok(userService.save(user));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    @PutMapping("/update")
//    public ResponseEntity<UserEntity> update(@RequestBody UserEntity user, @RequestParam UUID uuid) {
//        try {
//            return ResponseEntity.ok(userService.update(user, uuid));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    @DeleteMapping("/delete")
//    public ResponseEntity<String> delete(@RequestParam UUID uuid) {
//        try {
//            return ResponseEntity.ok(userService.delete(uuid));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }

}
