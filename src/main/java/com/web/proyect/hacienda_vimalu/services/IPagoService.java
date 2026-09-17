package com.web.proyect.hacienda_vimalu.services;

import com.web.proyect.hacienda_vimalu.dto.PagoDTO;

import java.util.List;
import java.util.Optional;

public interface IPagoService {

    List<PagoDTO> listarTodo();

    Optional<PagoDTO> buscarPorId(Long id);

    PagoDTO crear(PagoDTO pago);

    Optional<PagoDTO> actualizar(Long id, PagoDTO pago);

    boolean eliminar(Long id);
}