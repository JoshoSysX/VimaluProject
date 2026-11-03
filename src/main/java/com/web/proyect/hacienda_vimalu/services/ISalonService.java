package com.web.proyect.hacienda_vimalu.services;

import java.time.LocalDate;


import com.web.proyect.hacienda_vimalu.dto.SalonDTO;

import java.util.List;
import java.util.Optional;

public interface ISalonService {

    List<SalonDTO> listarTodo();

    Optional<SalonDTO> buscarPorId(Long id);

    SalonDTO crear(SalonDTO salon);

    Optional<SalonDTO> actualizar(Long id, SalonDTO salon);

    SalonDTO disponibilidad(Long id, LocalDate fecha);
    boolean eliminar(Long id);
}
