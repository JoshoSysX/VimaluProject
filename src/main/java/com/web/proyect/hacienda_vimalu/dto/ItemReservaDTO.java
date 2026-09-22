package com.web.proyect.hacienda_vimalu.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemReservaDTO(
        @NotNull(message = "El producto es obligatorio")
        Long idProducto,
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        Integer cantidad
) {
}
