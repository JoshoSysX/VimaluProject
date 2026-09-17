package com.web.proyect.hacienda_vimalu.controller;

import com.web.proyect.hacienda_vimalu.dto.DetalleReservaDTO;
import com.web.proyect.hacienda_vimalu.services.IDetalleReservaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detalles-reserva")
public class DetalleReservaController {

    private final IDetalleReservaService detalleService;

    public DetalleReservaController(
            IDetalleReservaService detalleService) {

        this.detalleService = detalleService;
    }

    @GetMapping
    public ResponseEntity<List<DetalleReservaDTO>> listarTodo() {
        return ResponseEntity.ok(
                detalleService.listarTodo()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleReservaDTO> buscarPorId(
            @PathVariable Long id) {

        return detalleService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DetalleReservaDTO> crear(
            @RequestBody DetalleReservaDTO detalle) {

        return ResponseEntity.ok(
                detalleService.crear(detalle)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleReservaDTO> actualizar(
            @PathVariable Long id,
            @RequestBody DetalleReservaDTO detalle) {

        return detalleService.actualizar(id, detalle)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        if (detalleService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
