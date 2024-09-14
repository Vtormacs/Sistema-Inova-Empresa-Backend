package com.Inova.Inova.Controller;

import com.Inova.Inova.Entities.EventEntity;
import com.Inova.Inova.Service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

}
