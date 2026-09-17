package com.web.proyect.hacienda_vimalu.controller;

import com.web.proyect.hacienda_vimalu.dto.MesaDTO;
import com.web.proyect.hacienda_vimalu.services.IMesaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mesas")
public class MesaController {

    private final IMesaService mesaService;

    public MesaController(IMesaService mesaService) {
        this.mesaService = mesaService;
    }

    @GetMapping
    public ResponseEntity<List<MesaDTO>> listarTodo() {
        return ResponseEntity.ok(mesaService.listarTodo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MesaDTO> buscarPorId(
            @PathVariable Long id) {

        return mesaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MesaDTO> crear(
            @RequestBody MesaDTO mesa) {

        return ResponseEntity.ok(
                mesaService.crear(mesa)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<MesaDTO> actualizar(
            @PathVariable Long id,
            @RequestBody MesaDTO mesa) {

        return mesaService.actualizar(id, mesa)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        if (mesaService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
