package com.web.proyect.hacienda_vimalu.controller;

import com.web.proyect.hacienda_vimalu.dto.RegistroDTO;
import com.web.proyect.hacienda_vimalu.dto.UsuarioDTO;
import com.web.proyect.hacienda_vimalu.services.IUsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final IUsuarioService usuarioService;

    public UsuarioController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioDTO> registrarCliente(
            @Valid @RequestBody RegistroDTO dto) {

        return usuarioService.registrarCliente(dto)
                .map(usuario -> ResponseEntity.status(HttpStatus.CREATED).body(usuario))
                .orElseGet(() ->
                        ResponseEntity.badRequest().build());
    }
}
