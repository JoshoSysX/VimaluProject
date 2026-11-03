package com.web.proyect.hacienda_vimalu.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public record PagoDTO(Long idPago, @NotNull Long idReserva,
        @NotNull @DecimalMin("0.01") @Digits(integer = 8, fraction = 2) BigDecimal monto,
        LocalDateTime fechaPago,
        @NotBlank @Size(max = 100) String tipoPago,
        @Size(max = 500) String comprobante,
        @Size(max = 30) String tipoComprobante,
        @NotBlank @Pattern(regexp = "PENDIENTE|PAGADO") String estado) {}