package com.web.proyect.hacienda_vimalu.services;

import com.web.proyect.hacienda_vimalu.dto.RegistroDTO;
import com.web.proyect.hacienda_vimalu.dto.UsuarioDTO;

import java.util.Optional;

public interface IUsuarioService {

    Optional<UsuarioDTO> registrarCliente(RegistroDTO dto);
}
