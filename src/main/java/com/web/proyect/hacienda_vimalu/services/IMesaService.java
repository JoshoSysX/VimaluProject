package com.web.proyect.hacienda_vimalu.services;

import com.web.proyect.hacienda_vimalu.dto.MesaDTO;

import java.util.List;
import java.util.Optional;

public interface IMesaService {

    List<MesaDTO> listarTodo();

    Optional<MesaDTO> buscarPorId(Long id);

    MesaDTO crear(MesaDTO mesa);

    Optional<MesaDTO> actualizar(Long id, MesaDTO mesa);

    boolean eliminar(Long id);
}
