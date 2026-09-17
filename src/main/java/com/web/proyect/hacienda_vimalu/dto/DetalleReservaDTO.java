package com.web.proyect.hacienda_vimalu.dto;

import java.math.BigDecimal;

public record DetalleReservaDTO(
        Long idDetalle,

        Long idReserva,

        Long idProducto,

        Integer cantidad,

        BigDecimal precioCompra,

        BigDecimal subtotal
) {
}
