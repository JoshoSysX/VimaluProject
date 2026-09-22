package com.web.proyect.hacienda_vimalu.dto;

import java.util.List;

public record ReservaResponseDTO(
        ReservaDTO reserva,
        List<DetalleReservaDTO> detalles
) {
}
