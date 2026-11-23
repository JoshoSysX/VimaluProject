package com.web.proyect.hacienda_vimalu.dto;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
public record MesaDTO(Long idMesa, @NotNull Long idSalon,
        @NotBlank @Size(max = 50) String nombre, @NotNull @Min(1) Integer capacidad,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) LocalDate fecha,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) Boolean disponible) {}
