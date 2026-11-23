package com.web.proyect.hacienda_vimalu.dto;

public record AuthResponse(
        String token,
        String usuario,
        String rol,
        Long idPersona
) {
}
