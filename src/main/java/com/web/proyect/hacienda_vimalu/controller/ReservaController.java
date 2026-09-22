package com.web.proyect.hacienda_vimalu.controller;

import com.web.proyect.hacienda_vimalu.dto.ReservaDTO;
import com.web.proyect.hacienda_vimalu.dto.ReservaResponseDTO;
import com.web.proyect.hacienda_vimalu.dto.SolicitudReservaDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import com.web.proyect.hacienda_vimalu.services.IReservaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final IReservaService reservaService;

    public ReservaController(IReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public ResponseEntity<List<ReservaDTO>> listarTodo() {
        return ResponseEntity.ok(
                reservaService.listarTodo()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO> buscarPorId(
            @PathVariable Long id) {

        return reservaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crear(
            @Valid @RequestBody SolicitudReservaDTO reserva) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                reservaService.crear(reserva)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ReservaDTO reserva) {

        return reservaService.actualizar(id, reserva)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        if (reservaService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
