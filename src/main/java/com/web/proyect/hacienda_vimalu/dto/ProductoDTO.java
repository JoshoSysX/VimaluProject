package com.web.proyect.hacienda_vimalu.dto;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductoDTO(
        @Parameter(hidden = true)
        Long idProducto,

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        String descripcion,

        String categoria,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.01")
        BigDecimal precio,

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0)
        Integer stock,

        String imagen
) {
}
