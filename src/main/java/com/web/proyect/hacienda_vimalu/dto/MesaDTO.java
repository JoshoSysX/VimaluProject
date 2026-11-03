package com.web.proyect.hacienda_vimalu.dto;
import jakarta.validation.constraints.*;
public record MesaDTO(Long idMesa, @NotBlank @Size(max = 50) String nombre,
        @NotNull @Min(1) Integer capacidad, @io.swagger.v3.oas.annotations.media.Schema(description = "Cantidad de mesas disponibles sin asignar a salones")
        @NotNull @Min(0) Integer cantidad) {}