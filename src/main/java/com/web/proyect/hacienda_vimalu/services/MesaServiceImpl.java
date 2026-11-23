package com.web.proyect.hacienda_vimalu.services;
import com.web.proyect.hacienda_vimalu.dto.MesaDTO;
import com.web.proyect.hacienda_vimalu.entity.*;
import com.web.proyect.hacienda_vimalu.repository.*;
import com.web.proyect.hacienda_vimalu.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;

@Service
public class MesaServiceImpl implements IMesaService {
    private final MesaRepository mesaRepository;
    private final SalonRepository salonRepository;
    private final ReservaRepository reservaRepository;
    private static final List<EstadoReserva> ACTIVAS = List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADO);
    public MesaServiceImpl(MesaRepository mesaRepository, SalonRepository salonRepository, ReservaRepository reservaRepository) {
        this.mesaRepository = mesaRepository;
        this.salonRepository = salonRepository;
        this.reservaRepository = reservaRepository;
    }
    @Override @Transactional(readOnly = true)
    public List<MesaDTO> listarTodo(Long idSalon, LocalDate fecha) {
        return (idSalon == null ? mesaRepository.findAll() : mesaRepository.findBySalon_IdSalon(idSalon))
                .stream().map(m -> convertToDTO(m, fecha)).toList();
    }
    @Override @Transactional(readOnly = true)
    public Optional<MesaDTO> buscarPorId(Long id, LocalDate fecha) {
        return mesaRepository.findById(id).map(m -> convertToDTO(m, fecha));
    }
    @Override @Transactional
    public MesaDTO crear(MesaDTO dto) {
        Salon salon = bloquearSalon(dto.idSalon());
        Mesa mesa = new Mesa();
        mesa.setSalon(salon);
        mesa.setNombre(dto.nombre());
        mesa.setCapacidad(dto.capacidad());
        mesaRepository.saveAndFlush(mesa);
        actualizarCantidad(salon);
        return convertToDTO(mesa, null);
    }
    @Override @Transactional
    public Optional<MesaDTO> actualizar(Long id, MesaDTO dto) {
        return mesaRepository.buscarParaActualizar(id).map(mesa -> {
            Long anterior = mesa.getSalon().getIdSalon();
            // Bloqueamos los salones por ID para mantener el mismo orden en los traslados.
            Salon primero = bloquearSalon(Math.min(anterior, dto.idSalon()));
            Salon segundo = anterior.equals(dto.idSalon()) ? primero : bloquearSalon(Math.max(anterior, dto.idSalon()));
            Salon destino = primero.getIdSalon().equals(dto.idSalon()) ? primero : segundo;
            if (!anterior.equals(dto.idSalon()) && reservaRepository.existsByMesa_IdMesa(id)) {
                throw new ReglaNegocioException("No se traslada una mesa que tiene reservas registradas");
            }
            LocalDate hoy = LocalDate.now(ZoneId.of("America/Lima"));
            boolean noAlcanza = reservaRepository.findByMesa_IdMesaAndEstadoReservaIn(id, ACTIVAS).stream()
                    .anyMatch(r -> !r.getFechaReserva().isBefore(hoy) && r.getCantidadPersonas() > dto.capacidad());
            if (noAlcanza) throw new ReglaNegocioException("La capacidad no alcanza para una reserva activa de esta mesa");
            mesa.setNombre(dto.nombre());
            mesa.setCapacidad(dto.capacidad());
            mesa.setSalon(destino);
            mesaRepository.saveAndFlush(mesa);
            actualizarCantidad(primero);
            if (segundo != primero) actualizarCantidad(segundo);
            return convertToDTO(mesa, null);
        });
    }
    @Override @Transactional
    public boolean eliminar(Long id) {
        return mesaRepository.buscarParaActualizar(id).map(mesa -> {
            Salon salon = bloquearSalon(mesa.getSalon().getIdSalon());
            if (reservaRepository.existsByMesa_IdMesa(id)) {
                throw new ReglaNegocioException("La mesa tiene reservas registradas y no puede eliminarse");
            }
            mesaRepository.delete(mesa);
            mesaRepository.flush();
            actualizarCantidad(salon);
            return true;
        }).orElse(false);
    }
    private Salon bloquearSalon(Long id) {
        return salonRepository.buscarParaActualizar(id).orElseThrow(() -> new ResourceNotFoundException("Salón no encontrado"));
    }
    private void actualizarCantidad(Salon salon) {
        salon.setCantidadMesas(Math.toIntExact(mesaRepository.countBySalon_IdSalon(salon.getIdSalon())));
        salonRepository.save(salon);
    }
    private MesaDTO convertToDTO(Mesa mesa, LocalDate fecha) {
        LocalDate dia = fecha == null ? LocalDate.now(ZoneId.of("America/Lima")) : fecha;
        boolean disponible = "DISPONIBLE".equals(mesa.getSalon().getEstado())
                && reservaRepository.findByMesa_IdMesaAndFechaReservaAndEstadoReservaIn(mesa.getIdMesa(), dia, ACTIVAS).isEmpty();
        return new MesaDTO(mesa.getIdMesa(), mesa.getSalon().getIdSalon(), mesa.getNombre(), mesa.getCapacidad(), dia, disponible);
    }
}