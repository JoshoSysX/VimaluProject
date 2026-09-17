package com.web.proyect.hacienda_vimalu.dto;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PersonaDTO(
        @Parameter(hidden = true)
        Long idPersona,

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El DNI es obligatorio")
        String dni,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Debe ser un email válido")
        String email,

        String telefono
) {
}
