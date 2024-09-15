package com.Inova.Inova.Controller;

import com.Inova.Inova.Entities.EventEntity;
import com.Inova.Inova.Service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping("/criar-evento")
    public ResponseEntity<EventEntity> criarEvento(@RequestBody EventEntity evento) {
        try {
            return ResponseEntity.ok(eventService.criarEvento(evento));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/selecao-de-jurados")
    public ResponseEntity<EventEntity> selecaoDeJurados(@RequestBody List<UUID> ids, @RequestParam UUID idDoEvento) {
        try {
            EventEntity eventoAtualizado = eventService.selecaoDeJurados(ids, idDoEvento);
            return ResponseEntity.ok(eventoAtualizado);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/listar-eventos")
    public ResponseEntity<List<EventEntity>> findAll(){
        try {
            return ResponseEntity.ok(eventService.findAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
