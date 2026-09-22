package com.web.proyect.hacienda_vimalu.dto;

import com.web.proyect.hacienda_vimalu.entity.Rol;

public record UsuarioDTO(
        Long idUsuario,
        String usuario,
        Rol rol,
        Long idPersona
) {
}
