package com.web.proyect.hacienda_vimalu.controller;

import com.web.proyect.hacienda_vimalu.dto.PagoDTO;
import com.web.proyect.hacienda_vimalu.services.IPagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final IPagoService pagoService;

    public PagoController(IPagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping
    public ResponseEntity<List<PagoDTO>> listarTodo() {
        return ResponseEntity.ok(
                pagoService.listarTodo()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoDTO> buscarPorId(
            @PathVariable Long id) {

        return pagoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PagoDTO> crear(
            @RequestBody PagoDTO pago) {

        return ResponseEntity.ok(
                pagoService.crear(pago)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagoDTO> actualizar(
            @PathVariable Long id,
            @RequestBody PagoDTO pago) {

        return pagoService.actualizar(id, pago)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        if (pagoService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
