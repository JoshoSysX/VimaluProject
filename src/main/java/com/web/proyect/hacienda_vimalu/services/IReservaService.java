package com.web.proyect.hacienda_vimalu.services;

import com.web.proyect.hacienda_vimalu.dto.ReservaDTO;

import java.util.List;
import java.util.Optional;

public interface IReservaService {

    List<ReservaDTO> listarTodo();

    Optional<ReservaDTO> buscarPorId(Long id);

    ReservaDTO crear(ReservaDTO reserva);

    Optional<ReservaDTO> actualizar(Long id, ReservaDTO reserva);

    boolean eliminar(Long id);
}
