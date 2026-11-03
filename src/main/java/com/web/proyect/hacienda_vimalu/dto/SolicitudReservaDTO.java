package com.web.proyect.hacienda_vimalu.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.List;

public record SolicitudReservaDTO(
        @NotBlank @jakarta.validation.constraints.Size(max = 100) String nombre,
        @NotNull(message = "La persona es obligatoria")
        Long idPersona,
        @NotNull(message = "El salón es obligatorio")
        Long idSalon,
        @NotNull(message = "La fecha de reserva es obligatoria")
        LocalDate fechaReserva,
        @NotBlank(message = "La hora de reserva es obligatoria")
        @Pattern(regexp = "([01]\\d|2[0-3]):[0-5]\\d", message = "La hora debe tener formato HH:mm")
        String horaReserva,
        @NotNull(message = "La cantidad de personas es obligatoria")
        @Min(value = 1, message = "Debe ser al menos 1 persona")
        Integer cantPersonas,
        String motivo,
        List<@NotNull @Valid ItemReservaDTO> items
) {
}
