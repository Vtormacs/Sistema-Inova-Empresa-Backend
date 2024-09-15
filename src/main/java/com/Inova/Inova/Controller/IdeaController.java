package com.Inova.Inova.Controller;

import com.Inova.Inova.Entities.IdeaEntity;
import com.Inova.Inova.Service.IdeaService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/idea")
public class IdeaController {

    @Autowired
    private IdeaService ideaService;

    @PostMapping("/postar-ideia")
    public ResponseEntity<IdeaEntity> postarIdeia(@RequestBody IdeaEntity ideia, @RequestParam UUID id_evento) {
        try {
            return ResponseEntity.ok(ideaService.postarIdeia(ideia, id_evento));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/listar-ideias")
    public ResponseEntity<List<IdeaEntity>> findAll() {
        try {
            return ResponseEntity.ok(ideaService.findAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/distribuir-ideias")
    public ResponseEntity<Void> distribuirIdeias(@RequestParam UUID id_evento) {
        try {
            ideaService.distribuirIdeias(id_evento);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
