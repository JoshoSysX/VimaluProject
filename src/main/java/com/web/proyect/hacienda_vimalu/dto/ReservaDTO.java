package com.web.proyect.hacienda_vimalu.dto;

import com.web.proyect.hacienda_vimalu.entity.EstadoReserva;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservaDTO(
        @Parameter(hidden = true)
        Long idReserva,
        @NotNull(message = "La fecha de reserva es obligatoria")
        LocalDate fechaReserva,
        @NotBlank(message = "La hora de reserva es obligatoria")
        @Schema(example = "13:30", description = "Hora en formato HH:mm de 24 horas", minLength = 5, maxLength = 5)
        @Pattern(regexp = "([01]\\d|2[0-3]):[0-5]\\d", message = "La hora debe tener formato HH:mm")
        String horaReserva,
        @NotNull(message = "La cantidad de personas es obligatoria")
        @Min(value = 1, message = "debe ser al menos 1 persona")
        Integer cantidadPersonas,
        String motivo,
        @NotNull(message = "El estado de reserva es obligatorio")
        EstadoReserva estadoReserva,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        BigDecimal total,
        @NotNull(message = "La persona es obligatoria")
        Long idPersona,
        @NotNull(message = "La mesa es obligatoria")
        Long idMesa,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) BigDecimal totalPagado,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) BigDecimal saldoPendiente
) {
}
