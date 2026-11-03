package com.web.proyect.hacienda_vimalu.dto;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
public record SalonDTO(Long idSalon, @NotNull Long idMesa,
        @NotBlank @Size(max = 20) String nombre,
        @NotNull @Min(1) Integer cantidad,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) Integer capacidad,
        @NotBlank @Pattern(regexp = "DISPONIBLE|NO_DISPONIBLE") String estado,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) LocalDate fecha,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) Integer mesasOcupadas,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) Integer mesasDisponibles,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) Integer capacidadDisponible) {}
