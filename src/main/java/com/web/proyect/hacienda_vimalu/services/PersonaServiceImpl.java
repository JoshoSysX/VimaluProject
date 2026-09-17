package com.web.proyect.hacienda_vimalu.services;

import com.web.proyect.hacienda_vimalu.dto.PersonaDTO;
import com.web.proyect.hacienda_vimalu.entity.Persona;
import com.web.proyect.hacienda_vimalu.repository.PersonaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PersonaServiceImpl implements IPersonaService {

    private final PersonaRepository personaRepository;

    public PersonaServiceImpl(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonaDTO> listarTodo() {
        return personaRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PersonaDTO> buscarPorId(Long id) {
        return personaRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public PersonaDTO crear(PersonaDTO p) {

        Persona persona = new Persona();

        persona.setNombre(p.nombre());
        persona.setDni(p.dni());
        persona.setEmail(p.email());
        persona.setTelefono(p.telefono());

        return convertToDTO(personaRepository.save(persona));
    }

    @Override
    @Transactional
    public Optional<PersonaDTO> actualizar(Long id, PersonaDTO p) {

        return personaRepository.findById(id)
                .map(persona -> {

                    persona.setNombre(p.nombre());
                    persona.setDni(p.dni());
                    persona.setEmail(p.email());
                    persona.setTelefono(p.telefono());

                    return convertToDTO(personaRepository.save(persona));
                });
    }

    @Override
    @Transactional
    public boolean eliminar(Long id) {

        if (personaRepository.existsById(id)) {
            personaRepository.deleteById(id);
            return true;
        }

        return false;
    }

    private PersonaDTO convertToDTO(Persona p) {

        return new PersonaDTO(
                p.getIdPersona(),
                p.getNombre(),
                p.getDni(),
                p.getEmail(),
                p.getTelefono()
        );
    }
}
