package com.web.proyect.hacienda_vimalu.services;

import com.web.proyect.hacienda_vimalu.dto.ReservaDTO;
import com.web.proyect.hacienda_vimalu.entity.Mesa;
import com.web.proyect.hacienda_vimalu.entity.Persona;
import com.web.proyect.hacienda_vimalu.entity.Reserva;
import com.web.proyect.hacienda_vimalu.repository.MesaRepository;
import com.web.proyect.hacienda_vimalu.repository.PersonaRepository;
import com.web.proyect.hacienda_vimalu.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaServiceImpl implements IReservaService {

    private final ReservaRepository reservaRepository;
    private final PersonaRepository personaRepository;
    private final MesaRepository mesaRepository;

    public ReservaServiceImpl(
            ReservaRepository reservaRepository,
            PersonaRepository personaRepository,
            MesaRepository mesaRepository) {

        this.reservaRepository = reservaRepository;
        this.personaRepository = personaRepository;
        this.mesaRepository = mesaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaDTO> listarTodo() {

        return reservaRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReservaDTO> buscarPorId(Long id) {

        return reservaRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public ReservaDTO crear(ReservaDTO r) {

        Persona persona = personaRepository.findById(r.idPersona())
                .orElseThrow(() ->
                        new RuntimeException("Persona no encontrada"));

        Mesa mesa = mesaRepository.findById(r.idMesa())
                .orElseThrow(() ->
                        new RuntimeException("Mesa no encontrada"));

        Reserva reserva = new Reserva();

        reserva.setFechaReserva(r.fechaReserva());
        reserva.setHoraReserva(r.horaReserva());
        reserva.setCantPersonas(r.cantPersonas());
        reserva.setMotivo(r.motivo());
        reserva.setEstadoReserva(r.estadoReserva());
        reserva.setTotal(r.total());
        reserva.setPersona(persona);
        reserva.setMesa(mesa);

        return convertToDTO(
                reservaRepository.save(reserva)
        );
    }

    @Override
    @Transactional
    public Optional<ReservaDTO> actualizar(Long id, ReservaDTO r) {

        return reservaRepository.findById(id)
                .map(reserva -> {

                    Persona persona = personaRepository.findById(r.idPersona())
                            .orElseThrow(() ->
                                    new RuntimeException("Persona no encontrada"));

                    Mesa mesa = mesaRepository.findById(r.idMesa())
                            .orElseThrow(() ->
                                    new RuntimeException("Mesa no encontrada"));

                    reserva.setFechaReserva(r.fechaReserva());
                    reserva.setHoraReserva(r.horaReserva());
                    reserva.setCantPersonas(r.cantPersonas());
                    reserva.setMotivo(r.motivo());
                    reserva.setEstadoReserva(r.estadoReserva());
                    reserva.setTotal(r.total());
                    reserva.setPersona(persona);
                    reserva.setMesa(mesa);

                    return convertToDTO(
                            reservaRepository.save(reserva)
                    );
                });
    }

    @Override
    @Transactional
    public boolean eliminar(Long id) {

        if (reservaRepository.existsById(id)) {
            reservaRepository.deleteById(id);
            return true;
        }

        return false;
    }

    private ReservaDTO convertToDTO(Reserva r) {

        return new ReservaDTO(
                r.getIdReserva(),
                r.getFechaReserva(),
                r.getHoraReserva(),
                r.getCantPersonas(),
                r.getMotivo(),
                r.getEstadoReserva(),
                r.getTotal(),
                r.getPersona().getIdPersona(),
                r.getMesa().getIdMesa()
        );
    }
}
