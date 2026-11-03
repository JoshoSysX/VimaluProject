package com.web.proyect.hacienda_vimalu.services;

import com.web.proyect.hacienda_vimalu.dto.*;
import com.web.proyect.hacienda_vimalu.entity.*;
import com.web.proyect.hacienda_vimalu.repository.*;
import com.web.proyect.hacienda_vimalu.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
public class SalonServiceImpl implements ISalonService {
    private final SalonRepository salonRepository;
    private final MesaRepository mesaRepository;
    private final ReservaRepository reservaRepository;
    private static final List<EstadoReserva> ACTIVAS = List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADO);

    public SalonServiceImpl(SalonRepository salonRepository, MesaRepository mesaRepository,
                            ReservaRepository reservaRepository) {
        this.salonRepository = salonRepository;
        this.mesaRepository = mesaRepository;
        this.reservaRepository = reservaRepository;
    }

    @Override @Transactional(readOnly = true)
    public List<SalonDTO> listarTodo() {
        return salonRepository.findAll().stream().map(this::convertToDTO).toList();
    }
    @Override @Transactional(readOnly = true)
    public Optional<SalonDTO> buscarPorId(Long id) {
        return salonRepository.findById(id).map(this::convertToDTO);
    }
    @Override @Transactional
    public SalonDTO crear(SalonDTO dto) {
        Salon salon = new Salon();
        asignarDatos(salon, dto);
        return convertToDTO(salonRepository.save(salon));
    }
    @Override @Transactional
    public Optional<SalonDTO> actualizar(Long id, SalonDTO dto) {
        return salonRepository.buscarParaActualizar(id).map(salon -> {
            asignarDatos(salon, dto);
            return convertToDTO(salonRepository.save(salon));
        });
    }
    private void asignarDatos(Salon salon, SalonDTO dto) {
        Long idAnterior = salon.getIdSalon() == null ? null : salon.getMesa().getIdMesa();
        int cantidadAnterior = salon.getIdSalon() == null ? 0 : salon.getCantidad();
        Mesa anterior = null;
        Mesa mesa;
        // Al cambiar de tipo, bloquear ambos inventarios en el mismo orden.
        if (idAnterior != null && !idAnterior.equals(dto.idMesa())) {
            Long primero = Math.min(idAnterior, dto.idMesa());
            Long segundo = Math.max(idAnterior, dto.idMesa());
            Mesa primera = buscarMesa(primero);
            Mesa segunda = buscarMesa(segundo);
            anterior = idAnterior.equals(primero) ? primera : segunda;
            mesa = dto.idMesa().equals(primero) ? primera : segunda;
        } else {
            mesa = buscarMesa(dto.idMesa());
        }
        boolean mismoTipo = dto.idMesa().equals(idAnterior);
        int disponibles = mesa.getCantidad() + (mismoTipo ? cantidadAnterior : 0);
        if (dto.cantidad() == null || dto.cantidad() < 1 || dto.cantidad() > disponibles) {
            throw new ReglaNegocioException("La cantidad supera las mesas disponibles de este tipo: " + disponibles);
        }
        if (salon.getIdSalon() != null) {
            List<Reserva> activas = reservaRepository.findBySalon_IdSalonAndEstadoReservaIn(salon.getIdSalon(), ACTIVAS);
            for (Reserva reserva : activas) {
                if (reserva.getCantPersonas() > mesa.getCapacidad()) {
                    throw new ReglaNegocioException("El tipo de mesa no alcanza para una reserva activa");
                }
                long ocupadas = activas.stream().filter(r -> r.getFechaReserva().equals(reserva.getFechaReserva())).count();
                if (ocupadas > dto.cantidad()) {
                    throw new ReglaNegocioException("No se pueden retirar mesas que tienen reservas activas");
                }
            }
        }
        // cantidad en Mesa es el inventario libre, no el total historico.
        if (anterior != null) {
            anterior.setCantidad(anterior.getCantidad() + cantidadAnterior);
            mesaRepository.save(anterior);
        }
        mesa.setCantidad(disponibles - dto.cantidad());
        mesaRepository.save(mesa);
        salon.setMesa(mesa);
        salon.setNombre(dto.nombre());
        salon.setCantidad(dto.cantidad());
        salon.setCapacidad(Math.multiplyExact(mesa.getCapacidad(), dto.cantidad()));
        salon.setEstado(dto.estado());
    }
    @Override @Transactional(readOnly = true)
    public SalonDTO disponibilidad(Long id, LocalDate fecha) {
        Salon salon = salonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salón no encontrado"));
        int ocupadas = reservaRepository.findBySalon_IdSalonAndFechaReservaAndEstadoReservaIn(id, fecha, ACTIVAS).size();
        int libres = "DISPONIBLE".equals(salon.getEstado()) ? Math.max(0, salon.getCantidad() - ocupadas) : 0;
        return new SalonDTO(id, salon.getMesa().getIdMesa(), salon.getNombre(),
                salon.getCantidad(), salon.getCapacidad(), salon.getEstado(),
                fecha, ocupadas, libres, libres * salon.getMesa().getCapacidad());
    }
    @Override @Transactional
    public boolean eliminar(Long id) {
        return salonRepository.buscarParaActualizar(id).map(salon -> {
            if (reservaRepository.existsBySalon_IdSalon(id)) {
                throw new ReglaNegocioException("El salón tiene reservas registradas y no puede eliminarse");
            }
            Mesa mesa = buscarMesa(salon.getMesa().getIdMesa());
            mesa.setCantidad(mesa.getCantidad() + salon.getCantidad());
            mesaRepository.save(mesa);
            salonRepository.delete(salon);
            return true;
        }).orElse(false);
    }
    private Mesa buscarMesa(Long id) {
        return mesaRepository.buscarParaActualizar(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de mesa no encontrado"));
    }
    private SalonDTO convertToDTO(Salon salon) {
        return new SalonDTO(salon.getIdSalon(), salon.getMesa().getIdMesa(), salon.getNombre(),
                salon.getCantidad(), salon.getCapacidad(), salon.getEstado(), null, null, null, null);
    }
}
