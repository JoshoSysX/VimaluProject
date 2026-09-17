package com.web.proyect.hacienda_vimalu.services;

import com.web.proyect.hacienda_vimalu.dto.MesaDTO;
import com.web.proyect.hacienda_vimalu.entity.Mesa;
import com.web.proyect.hacienda_vimalu.repository.MesaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MesaServiceImpl implements IMesaService {

    private final MesaRepository mesaRepository;

    public MesaServiceImpl(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MesaDTO> listarTodo() {

        return mesaRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MesaDTO> buscarPorId(Long id) {

        return mesaRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public MesaDTO crear(MesaDTO m) {

        Mesa mesa = new Mesa();

        mesa.setCapacidad(m.capacidad());
        mesa.setNroMesa(m.nroMesa());
        mesa.setEstadoMesa(m.estadoMesa());

        return convertToDTO(mesaRepository.save(mesa));
    }

    @Override
    @Transactional
    public Optional<MesaDTO> actualizar(Long id, MesaDTO m) {

        return mesaRepository.findById(id)
                .map(mesa -> {

                    mesa.setCapacidad(m.capacidad());
                    mesa.setNroMesa(m.nroMesa());
                    mesa.setEstadoMesa(m.estadoMesa());

                    return convertToDTO(mesaRepository.save(mesa));
                });
    }

    @Override
    @Transactional
    public boolean eliminar(Long id) {

        if (mesaRepository.existsById(id)) {
            mesaRepository.deleteById(id);
            return true;
        }

        return false;
    }

    private MesaDTO convertToDTO(Mesa m) {

        return new MesaDTO(
                m.getIdMesa(),
                m.getCapacidad(),
                m.getNroMesa(),
                m.getEstadoMesa()
        );
    }
}
