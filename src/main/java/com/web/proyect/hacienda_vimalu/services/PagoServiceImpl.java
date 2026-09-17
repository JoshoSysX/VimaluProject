package com.web.proyect.hacienda_vimalu.services;

import com.web.proyect.hacienda_vimalu.dto.PagoDTO;
import com.web.proyect.hacienda_vimalu.entity.Pago;
import com.web.proyect.hacienda_vimalu.entity.Reserva;
import com.web.proyect.hacienda_vimalu.repository.PagoRepository;
import com.web.proyect.hacienda_vimalu.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PagoServiceImpl implements IPagoService {

    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;

    public PagoServiceImpl(
            PagoRepository pagoRepository,
            ReservaRepository reservaRepository) {

        this.pagoRepository = pagoRepository;
        this.reservaRepository = reservaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoDTO> listarTodo() {

        return pagoRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PagoDTO> buscarPorId(Long id) {

        return pagoRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public PagoDTO crear(PagoDTO p) {

        Reserva reserva = reservaRepository
                .findById(p.idReserva())
                .orElseThrow(() ->
                        new RuntimeException("Reserva no encontrada"));

        Pago pago = new Pago();

        pago.setReserva(reserva);
        pago.setComprobante(p.comprobante());

        return convertToDTO(
                pagoRepository.save(pago)
        );
    }

    @Override
    @Transactional
    public Optional<PagoDTO> actualizar(
            Long id,
            PagoDTO p) {

        return pagoRepository.findById(id)
                .map(pago -> {

                    Reserva reserva = reservaRepository
                            .findById(p.idReserva())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Reserva no encontrada"));

                    pago.setReserva(reserva);
                    pago.setComprobante(p.comprobante());

                    return convertToDTO(
                            pagoRepository.save(pago)
                    );
                });
    }

    @Override
    @Transactional
    public boolean eliminar(Long id) {

        if (pagoRepository.existsById(id)) {
            pagoRepository.deleteById(id);
            return true;
        }

        return false;
    }

    private PagoDTO convertToDTO(Pago p) {

        return new PagoDTO(
                p.getIdPago(),
                p.getReserva().getIdReserva(),
                p.getComprobante()
        );
    }
}