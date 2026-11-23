package com.web.proyect.hacienda_vimalu.dto;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record SalonDTO(Long idSalon,
                       @NotBlank @Size(max = 50)
                       String nombre,
                       @Schema(accessMode = Schema.AccessMode.READ_ONLY)
                       Integer cantidadMesas,
                       @Schema(accessMode = Schema.AccessMode.READ_ONLY)
                       Integer capacidad,
                       @NotBlank @Pattern(regexp = "DISPONIBLE|NO_DISPONIBLE")
                       String estado,
                       @Schema(accessMode = Schema.AccessMode.READ_ONLY)
                       LocalDate fecha,
                       @Schema(accessMode = Schema.AccessMode.READ_ONLY)
                       Integer mesasOcupadas,
                       @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Mesas libres para la fecha consultada")
                       Integer mdisponible,
                       @Schema(accessMode = Schema.AccessMode.READ_ONLY)
                       Integer capacidadDisponible) {
}
