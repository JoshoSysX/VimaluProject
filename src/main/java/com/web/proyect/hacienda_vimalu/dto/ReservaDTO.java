package com.web.proyect.hacienda_vimalu.dto;

import com.web.proyect.hacienda_vimalu.entity.EstadoReserva;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservaDTO(
        Long idReserva,

        LocalDate fechaReserva,

        String horaReserva,

        Integer cantPersonas,

        String motivo,

        EstadoReserva estadoReserva,

        BigDecimal total,

        Long idPersona,

        Long idMesa
) {
}
