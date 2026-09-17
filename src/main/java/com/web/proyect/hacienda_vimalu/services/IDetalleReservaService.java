package com.web.proyect.hacienda_vimalu.services;

import com.web.proyect.hacienda_vimalu.dto.DetalleReservaDTO;

import java.util.List;
import java.util.Optional;

public interface IDetalleReservaService {

    List<DetalleReservaDTO> listarTodo();

    Optional<DetalleReservaDTO> buscarPorId(Long id);

    DetalleReservaDTO crear(DetalleReservaDTO detalle);

    Optional<DetalleReservaDTO> actualizar(
            Long id,
            DetalleReservaDTO detalle
    );

    boolean eliminar(Long id);
}