package com.web.proyect.hacienda_vimalu.services;

import com.web.proyect.hacienda_vimalu.dto.PersonaDTO;

import java.util.List;
import java.util.Optional;

public interface IPersonaService {
    List<PersonaDTO> listarTodo();

    Optional<PersonaDTO> buscarPorId(Long id);

    PersonaDTO crear(PersonaDTO p);

    Optional<PersonaDTO> actualizar(Long id, PersonaDTO p);

    boolean eliminar(Long id);
}
