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

    @GetMapping("/top-ideias-votadas")
    public ResponseEntity<List<IdeaEntity>> top10Votados() {
        try {
            return ResponseEntity.ok(ideaService.top10Votados());
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

    @PostMapping("/avaliar")
    public ResponseEntity<String> avaliarIdeia(@RequestParam UUID id_ideia, @RequestParam UUID id_jurado, @RequestParam int nota) {
        try {
            ideaService.avaliarIdeia(id_ideia, id_jurado, nota);
            return ResponseEntity.ok("Avaliação registrada com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/voto-popular")
    public ResponseEntity<Void> votoPopular(@RequestParam UUID id_ideia, @RequestParam UUID id_colaborador) {
        try {
            ideaService.votoPopular(id_ideia, id_colaborador);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
