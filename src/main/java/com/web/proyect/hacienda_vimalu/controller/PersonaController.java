package com.web.proyect.hacienda_vimalu.controller;

import com.web.proyect.hacienda_vimalu.dto.PersonaDTO;
import com.web.proyect.hacienda_vimalu.services.IPersonaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private final IPersonaService personaService;

    public PersonaController(IPersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping
    public ResponseEntity<List<PersonaDTO>> listarTodo() {
        return ResponseEntity.ok(personaService.listarTodo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonaDTO> buscarPorId(
            @PathVariable Long id) {

        return personaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PersonaDTO> crear(
            @RequestBody PersonaDTO persona) {

        return ResponseEntity.ok(
                personaService.crear(persona)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonaDTO> actualizar(
            @PathVariable Long id,
            @RequestBody PersonaDTO persona) {

        return personaService.actualizar(id, persona)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        if (personaService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
