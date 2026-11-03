package com.web.proyect.hacienda_vimalu.services;

import com.web.proyect.hacienda_vimalu.exception.ReglaNegocioException;

import com.web.proyect.hacienda_vimalu.repository.SalonRepository;

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
    private final SalonRepository salonRepository;

    public MesaServiceImpl(MesaRepository mesaRepository, SalonRepository salonRepository) {
        this.mesaRepository = mesaRepository;
        this.salonRepository = salonRepository;
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
        mesa.setNombre(m.nombre());
        mesa.setCantidad(m.cantidad());

        return convertToDTO(mesaRepository.save(mesa));
    }

    @Override
    @Transactional
    public Optional<MesaDTO> actualizar(Long id, MesaDTO m) {

        return mesaRepository.buscarParaActualizar(id)
                .map(mesa -> {

                    var salones = salonRepository.findByMesa_IdMesa(id);
                    if (!salones.isEmpty() && !mesa.getCapacidad().equals(m.capacidad())) {
                        throw new ReglaNegocioException("No cambie la capacidad de un tipo de mesa ya asignado; cree otro tipo de mesa");
                    }
                    mesa.setCapacidad(m.capacidad());
                    mesa.setNombre(m.nombre());
                    mesa.setCantidad(m.cantidad());

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
                m.getNombre(), m.getCapacidad(), m.getCantidad()
        );
    }
}
