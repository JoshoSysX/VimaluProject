package com.web.proyect.hacienda_vimalu.services;

import com.web.proyect.hacienda_vimalu.dto.ReservaDTO;
import com.web.proyect.hacienda_vimalu.dto.ReservaResponseDTO;
import com.web.proyect.hacienda_vimalu.dto.SolicitudReservaDTO;

import java.util.List;
import java.util.Optional;

public interface IReservaService {

    List<ReservaDTO> listarTodo();

    Optional<ReservaDTO> buscarPorId(Long id);

    ReservaResponseDTO crear(SolicitudReservaDTO reserva);

    Optional<ReservaDTO> actualizar(Long id, ReservaDTO reserva);

    boolean eliminar(Long id);
}
