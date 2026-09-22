package com.web.proyect.hacienda_vimalu.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistroDTO(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El DNI es obligatorio")
        @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 dígitos")
        String dni,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Debe ser un email válido")
        String email,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "9\\d{8}", message = "El teléfono debe tener 9 dígitos y empezar en 9")
        String telefono,

        @NotBlank(message = "El usuario es obligatorio")
        @Size(min = 4, max = 20, message = "El usuario debe tener entre 4 y 20 caracteres")
        String usuario,

        @NotBlank(message = "La clave es obligatoria")
        @Size(min = 6, message = "La clave debe tener al menos 6 caracteres")
        String password
) {
}
