package com.web.proyect.hacienda_vimalu.controller;
import com.web.proyect.hacienda_vimalu.dto.MesaDTO;
import com.web.proyect.hacienda_vimalu.services.IMesaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/mesas")
public class MesaController {
    private final IMesaService mesaService;
    public MesaController(IMesaService mesaService) { this.mesaService = mesaService; }
    @GetMapping
    public ResponseEntity<List<MesaDTO>> listarTodo(@RequestParam(required = false) Long idSalon,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(mesaService.listarTodo(idSalon, fecha));
    }
    @GetMapping("/{id}")
    public ResponseEntity<MesaDTO> buscarPorId(@PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return mesaService.buscarPorId(id, fecha).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<MesaDTO> crear(@Valid @RequestBody MesaDTO dto) {
        return ResponseEntity.ok(mesaService.crear(dto));
    }
    @PutMapping("/{id}")
    public ResponseEntity<MesaDTO> actualizar(@PathVariable Long id, @Valid @RequestBody MesaDTO dto) {
        return mesaService.actualizar(id, dto).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return mesaService.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}