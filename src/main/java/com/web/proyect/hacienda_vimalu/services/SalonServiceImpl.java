package com.web.proyect.hacienda_vimalu.services;
import com.web.proyect.hacienda_vimalu.dto.SalonDTO;
import com.web.proyect.hacienda_vimalu.entity.*;
import com.web.proyect.hacienda_vimalu.repository.*;
import com.web.proyect.hacienda_vimalu.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class SalonServiceImpl implements ISalonService {
    private final SalonRepository salonRepository;
    private final MesaRepository mesaRepository;
    private final ReservaRepository reservaRepository;
    private static final List<EstadoReserva> ACTIVAS = List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADO);
    public SalonServiceImpl(SalonRepository salonRepository, MesaRepository mesaRepository, ReservaRepository reservaRepository) {
        this.salonRepository = salonRepository;
        this.mesaRepository = mesaRepository;
        this.reservaRepository = reservaRepository;
    }
    @Override @Transactional(readOnly = true)
    public List<SalonDTO> listarTodo(LocalDate fecha) {
        return salonRepository.findAll().stream().map(s -> convertToDTO(s, fechaConsulta(fecha))).toList();
    }
    @Override @Transactional(readOnly = true)
    public Optional<SalonDTO> buscarPorId(Long id, LocalDate fecha) {
        return salonRepository.findById(id).map(s -> convertToDTO(s, fechaConsulta(fecha)));
    }
    @Override @Transactional
    public SalonDTO crear(SalonDTO dto) {
        Salon salon = new Salon();
        salon.setNombre(dto.nombre());
        salon.setEstado(dto.estado());
        salon.setCantidadMesas(0);
        return convertToDTO(salonRepository.save(salon), fechaConsulta(null));
    }
    @Override @Transactional
    public Optional<SalonDTO> actualizar(Long id, SalonDTO dto) {
        return salonRepository.buscarParaActualizar(id).map(salon -> {
            salon.setNombre(dto.nombre());
            salon.setEstado(dto.estado());
            salon.setCantidadMesas(Math.toIntExact(mesaRepository.countBySalon_IdSalon(id)));
            return convertToDTO(salonRepository.save(salon), fechaConsulta(null));
        });
    }
    @Override @Transactional(readOnly = true)
    public SalonDTO disponibilidad(Long id, LocalDate fecha) {
        return buscarPorId(id, fecha).orElseThrow(() -> new ResourceNotFoundException("Salón no encontrado"));
    }
    @Override @Transactional
    public boolean eliminar(Long id) {
        return salonRepository.buscarParaActualizar(id).map(salon -> {
            if (mesaRepository.existsBySalon_IdSalon(id)) {
                throw new ReglaNegocioException("El salón tiene mesas registradas; retire primero las mesas sin reservas");
            }
            salonRepository.delete(salon);
            return true;
        }).orElse(false);
    }
    private LocalDate fechaConsulta(LocalDate fecha) {
        return fecha == null ? LocalDate.now(ZoneId.of("America/Lima")) : fecha;
    }
    private SalonDTO convertToDTO(Salon salon, LocalDate fecha) {
        List<Mesa> mesas = mesaRepository.findBySalon_IdSalon(salon.getIdSalon());
        Set<Long> ocupadas = new HashSet<>();
        reservaRepository.findByMesa_Salon_IdSalonAndFechaReservaAndEstadoReservaIn(salon.getIdSalon(), fecha, ACTIVAS)
                .forEach(r -> ocupadas.add(r.getMesa().getIdMesa()));
        List<Mesa> libres = "DISPONIBLE".equals(salon.getEstado())
                ? mesas.stream().filter(m -> !ocupadas.contains(m.getIdMesa())).toList() : List.of();
        return new SalonDTO(salon.getIdSalon(), salon.getNombre(), mesas.size(),
                mesas.stream().mapToInt(Mesa::getCapacidad).sum(), salon.getEstado(), fecha,
                ocupadas.size(), libres.size(), libres.stream().mapToInt(Mesa::getCapacidad).sum());
    }
}