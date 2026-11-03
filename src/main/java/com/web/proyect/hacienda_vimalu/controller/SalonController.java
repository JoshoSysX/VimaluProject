package com.web.proyect.hacienda_vimalu.controller;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


import com.web.proyect.hacienda_vimalu.dto.SalonDTO;
import com.web.proyect.hacienda_vimalu.services.ISalonService;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salones")
public class SalonController {

    private final ISalonService salonService;

    public SalonController(ISalonService salonService) {
        this.salonService = salonService;
    }

    @GetMapping
    public ResponseEntity<List<SalonDTO>> listarTodo() {
        return ResponseEntity.ok(salonService.listarTodo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalonDTO> buscarPorId(
            @PathVariable Long id) {

        return salonService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/disponibilidad")
    public ResponseEntity<SalonDTO> disponibilidad(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(salonService.disponibilidad(id, fecha));
    }

    @PostMapping
    public ResponseEntity<SalonDTO> crear(
            @Valid @RequestBody SalonDTO salon) {

        return ResponseEntity.ok(
                salonService.crear(salon)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalonDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody SalonDTO salon) {

        return salonService.actualizar(id, salon)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        if (salonService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
