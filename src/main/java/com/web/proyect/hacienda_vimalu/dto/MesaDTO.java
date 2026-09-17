package com.web.proyect.hacienda_vimalu.dto;

import com.web.proyect.hacienda_vimalu.entity.EstadoMesa;

public record MesaDTO(
        Long idMesa,
        Integer capacidad,
        Integer nroMesa,
        EstadoMesa estadoMesa
) {
}
