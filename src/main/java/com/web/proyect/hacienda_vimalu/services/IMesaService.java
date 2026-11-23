package com.web.proyect.hacienda_vimalu.services;
import com.web.proyect.hacienda_vimalu.dto.MesaDTO;
import java.time.LocalDate;
import java.util.*;
public interface IMesaService {
    List<MesaDTO> listarTodo(Long idSalon, LocalDate fecha);
    Optional<MesaDTO> buscarPorId(Long id, LocalDate fecha);
    MesaDTO crear(MesaDTO dto);
    Optional<MesaDTO> actualizar(Long id, MesaDTO dto);
    boolean eliminar(Long id);
}