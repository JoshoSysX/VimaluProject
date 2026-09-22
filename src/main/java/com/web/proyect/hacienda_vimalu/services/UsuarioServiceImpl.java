package com.web.proyect.hacienda_vimalu.services;

import com.web.proyect.hacienda_vimalu.dto.RegistroDTO;
import com.web.proyect.hacienda_vimalu.dto.UsuarioDTO;
import com.web.proyect.hacienda_vimalu.entity.Persona;
import com.web.proyect.hacienda_vimalu.entity.Rol;
import com.web.proyect.hacienda_vimalu.entity.Usuario;
import com.web.proyect.hacienda_vimalu.repository.PersonaRepository;
import com.web.proyect.hacienda_vimalu.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.web.proyect.hacienda_vimalu.exception.DuplicateResourceException;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;

    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository,
            PersonaRepository personaRepository) {

        this.usuarioRepository = usuarioRepository;
        this.personaRepository = personaRepository;
    }

    @Override
    @Transactional
    public Optional<UsuarioDTO> registrarCliente(RegistroDTO dto) {

        if (personaRepository.existsByEmail(dto.email())) {
            throw new DuplicateResourceException("El email ya está registrado");
        }
        if (personaRepository.existsByDni(dto.dni())) {
            throw new DuplicateResourceException("El DNI ya está registrado");
        }
        if (usuarioRepository.existsByUsuario(dto.usuario())) {
            throw new DuplicateResourceException("El nombre de usuario ya existe");
        }

        Persona persona = new Persona();

        persona.setNombre(dto.nombre());
        persona.setDni(dto.dni());
        persona.setEmail(dto.email());
        persona.setTelefono(dto.telefono());

        Persona personaGuardada = personaRepository.save(persona);

        Usuario usuario = new Usuario();

        usuario.setUsuario(dto.usuario());
        usuario.setPassword(dto.password());
        usuario.setRol(Rol.CLIENTE);
        usuario.setPersona(personaGuardada);

        Usuario usuarioGuardado =
                usuarioRepository.save(usuario);

        UsuarioDTO usuarioDTO = new UsuarioDTO(
                usuarioGuardado.getIdUsuario(),
                usuarioGuardado.getUsuario(),
                usuarioGuardado.getRol(),
                usuarioGuardado.getPersona().getIdPersona()
        );

        return Optional.of(usuarioDTO);
    }
}
