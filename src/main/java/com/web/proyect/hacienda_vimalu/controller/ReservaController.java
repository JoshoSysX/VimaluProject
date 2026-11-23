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
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import com.web.proyect.hacienda_vimalu.security.UsuarioPrincipal;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final IReservaService reservaService;

    public ReservaController(IReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public ResponseEntity<List<ReservaDTO>> listarTodo(Authentication authentication) {
        return ResponseEntity.ok(
                reservaService.listarTodo().stream().filter(r -> puedeConsultar(authentication, r.idPersona())).toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO> buscarPorId(
            @PathVariable Long id, Authentication authentication) {

        return reservaService.buscarPorId(id)
                .map(reserva -> {
                    validarPersona(authentication, reserva.idPersona());
                    return ResponseEntity.ok(reserva);
                })
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crear(
            @Valid @RequestBody SolicitudReservaDTO reserva, Authentication authentication) {

        validarPersona(authentication, reserva.idPersona());

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

    private boolean puedeConsultar(Authentication auth, Long idPersona) {
        if (auth.getAuthorities().stream().anyMatch(a -> "ADMIN".equals(a.getAuthority()))) return true;
        return auth.getPrincipal() instanceof UsuarioPrincipal principal
                && principal.usuario().getPersona().getIdPersona().equals(idPersona);
    }

    private void validarPersona(Authentication auth, Long idPersona) {
        if (!puedeConsultar(auth, idPersona)) throw new AccessDeniedException("Solo puede usar sus propias reservas");
    }
}
