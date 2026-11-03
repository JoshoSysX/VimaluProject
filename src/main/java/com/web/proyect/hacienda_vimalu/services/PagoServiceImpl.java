package com.web.proyect.hacienda_vimalu.services;

import com.web.proyect.hacienda_vimalu.dto.PagoDTO;
import com.web.proyect.hacienda_vimalu.entity.Pago;
import com.web.proyect.hacienda_vimalu.entity.Reserva;
import com.web.proyect.hacienda_vimalu.repository.PagoRepository;
import com.web.proyect.hacienda_vimalu.repository.ReservaRepository;
import com.web.proyect.hacienda_vimalu.exception.ResourceNotFoundException;
import com.web.proyect.hacienda_vimalu.exception.ReglaNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PagoServiceImpl implements IPagoService {
    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;
    public PagoServiceImpl(PagoRepository pagoRepository, ReservaRepository reservaRepository) {
        this.pagoRepository = pagoRepository;
        this.reservaRepository = reservaRepository;
    }
    @Override @Transactional(readOnly = true)
    public List<PagoDTO> listarTodo() {
        return pagoRepository.findAll().stream().map(this::convertToDTO).toList();
    }
    @Override @Transactional(readOnly = true)
    public Optional<PagoDTO> buscarPorId(Long id) {
        return pagoRepository.findById(id).map(this::convertToDTO);
    }
    @Override @Transactional
    public PagoDTO crear(PagoDTO dto) {
        Pago pago = new Pago();
        asignarDatos(pago, dto);
        return convertToDTO(pagoRepository.save(pago));
    }
    @Override @Transactional
    public Optional<PagoDTO> actualizar(Long id, PagoDTO dto) {
        // Se bloquea la reserva antes de leer sus pagos para evitar cobros simultaneos.
        reservaRepository.buscarParaActualizar(dto.idReserva())
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
        return pagoRepository.findById(id).map(pago -> {
            if ("PAGADO".equals(pago.getEstado())) {
                throw new ReglaNegocioException("Un pago confirmado no se modifica ni se devuelve");
            }
            if (!pago.getReserva().getIdReserva().equals(dto.idReserva())) {
                throw new ReglaNegocioException("No se puede trasladar un pago a otra reserva");
            }
            if ("RESERVA".equals(pago.getTipoComprobante()) && dto.monto().compareTo(new BigDecimal("25.00")) != 0) {
                throw new ReglaNegocioException("El pago inicial de reserva debe mantenerse en S/25");
            }
            asignarDatos(pago, dto);
            return convertToDTO(pagoRepository.save(pago));
        });
    }
    private void asignarDatos(Pago pago, PagoDTO dto) {
        Reserva reserva = reservaRepository.buscarParaActualizar(dto.idReserva())
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
        BigDecimal pagado = BigDecimal.ZERO;
        for (Pago otro : pagoRepository.findByReserva_IdReserva(dto.idReserva())) {
            if (!Objects.equals(otro.getIdPago(), pago.getIdPago()) && "PAGADO".equals(otro.getEstado())) {
                pagado = pagado.add(otro.getMonto());
            }
        }
        if (dto.monto().compareTo(reserva.getTotal().subtract(pagado)) > 0) {
            throw new ReglaNegocioException("El monto supera el saldo pendiente de la reserva");
        }
        if ("PAGADO".equals(dto.estado()) && "POR_DEFINIR".equals(dto.tipoPago())) {
            throw new ReglaNegocioException("Indique el medio de pago utilizado");
        }
        pago.setReserva(reserva);
        pago.setMonto(dto.monto());
        pago.setEstado(dto.estado());
        pago.setFechaPago("PAGADO".equals(dto.estado())
                ? (dto.fechaPago() == null ? LocalDateTime.now() : dto.fechaPago()) : null);
        pago.setTipoPago(dto.tipoPago());
        pago.setComprobante(dto.comprobante());
        // RESERVA identifica el adelanto automatico; se conserva al confirmarlo.
        if (!"RESERVA".equals(pago.getTipoComprobante())) pago.setTipoComprobante(dto.tipoComprobante());
    }
    @Override @Transactional
    public boolean eliminar(Long id) {
        return pagoRepository.findById(id).map(pago -> {
            if ("PAGADO".equals(pago.getEstado()) || "RESERVA".equals(pago.getTipoComprobante())) {
                throw new ReglaNegocioException("No se elimina un pago confirmado ni el pago inicial de reserva");
            }
            pagoRepository.delete(pago);
            return true;
        }).orElse(false);
    }
    private PagoDTO convertToDTO(Pago pago) {
        return new PagoDTO(pago.getIdPago(), pago.getReserva().getIdReserva(), pago.getMonto(),
                pago.getFechaPago(), pago.getTipoPago(), pago.getComprobante(), pago.getTipoComprobante(), pago.getEstado());
    }
}