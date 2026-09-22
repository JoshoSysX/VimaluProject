package com.web.proyect.hacienda_vimalu.services;

import com.web.proyect.hacienda_vimalu.dto.ReservaDTO;
import com.web.proyect.hacienda_vimalu.dto.DetalleReservaDTO;
import com.web.proyect.hacienda_vimalu.dto.ItemReservaDTO;
import com.web.proyect.hacienda_vimalu.dto.ReservaResponseDTO;
import com.web.proyect.hacienda_vimalu.dto.SolicitudReservaDTO;
import com.web.proyect.hacienda_vimalu.entity.DetalleReserva;
import com.web.proyect.hacienda_vimalu.entity.EstadoReserva;
import com.web.proyect.hacienda_vimalu.entity.MesaReserva;
import com.web.proyect.hacienda_vimalu.entity.Producto;
import com.web.proyect.hacienda_vimalu.exception.ResourceNotFoundException;
import com.web.proyect.hacienda_vimalu.exception.InsufficientStockException;
import com.web.proyect.hacienda_vimalu.entity.Mesa;
import com.web.proyect.hacienda_vimalu.entity.Persona;
import com.web.proyect.hacienda_vimalu.entity.Reserva;
import com.web.proyect.hacienda_vimalu.repository.MesaRepository;
import com.web.proyect.hacienda_vimalu.repository.PersonaRepository;
import com.web.proyect.hacienda_vimalu.repository.ReservaRepository;
import com.web.proyect.hacienda_vimalu.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ReservaServiceImpl implements IReservaService {

    private final ReservaRepository reservaRepository;
    private final PersonaRepository personaRepository;
    private final MesaRepository mesaRepository;
    private final ProductoRepository productoRepository;

    public ReservaServiceImpl(
            ReservaRepository reservaRepository,
            PersonaRepository personaRepository,
            MesaRepository mesaRepository,
            ProductoRepository productoRepository) {

        this.reservaRepository = reservaRepository;
        this.personaRepository = personaRepository;
        this.mesaRepository = mesaRepository;
        this.productoRepository = productoRepository;
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
    public ReservaResponseDTO crear(SolicitudReservaDTO r) {

        Persona persona = personaRepository.findById(r.idPersona())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Persona no encontrada"));

        Mesa mesa = mesaRepository.findById(r.idMesa())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Mesa no encontrada"));

        Reserva reserva = new Reserva();

        reserva.setFechaReserva(r.fechaReserva());
        reserva.setHoraReserva(r.horaReserva());
        reserva.setCantPersonas(r.cantPersonas());
        reserva.setMotivo(r.motivo());
        reserva.setFechaCreacion(LocalDateTime.now());
        reserva.setEstadoReserva(EstadoReserva.PENDIENTE);
        reserva.setPersona(persona);
        reserva.getMesasReservadas().add(new MesaReserva(null, mesa, reserva));

        BigDecimal total = BigDecimal.ZERO;
        for (ItemReservaDTO item : r.items()) {
            Producto producto = productoRepository.findById(item.idProducto())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado: " + item.idProducto()));
            if (producto.getStock() < item.cantidad()) {
                throw new InsufficientStockException("Stock insuficiente para " + producto.getNombre());
            }
            producto.setStock(producto.getStock() - item.cantidad());
            productoRepository.save(producto);

            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(item.cantidad()));
            reserva.getDetalles().add(new DetalleReserva(
                    null, reserva, producto, item.cantidad(), producto.getPrecio(), subtotal));
            total = total.add(subtotal);
        }
        reserva.setTotal(total);
        Reserva guardada = reservaRepository.save(reserva);
        List<DetalleReservaDTO> detalles = guardada.getDetalles().stream()
                .map(d -> new DetalleReservaDTO(d.getIdDetalle(), guardada.getIdReserva(),
                        d.getProducto().getIdProducto(), d.getCantidad(), d.getPrecioCompra(), d.getSubtotal()))
                .toList();
        return new ReservaResponseDTO(convertToDTO(guardada), detalles);
    }

    @Override
    @Transactional
    public Optional<ReservaDTO> actualizar(Long id, ReservaDTO r) {

        return reservaRepository.findById(id)
                .map(reserva -> {

                    Persona persona = personaRepository.findById(r.idPersona())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Persona no encontrada"));

                    Mesa mesa = mesaRepository.findById(r.idMesa())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Mesa no encontrada"));

                    reserva.setFechaReserva(r.fechaReserva());
                    reserva.setHoraReserva(r.horaReserva());
                    reserva.setCantPersonas(r.cantPersonas());
                    reserva.setMotivo(r.motivo());
                    reserva.setEstadoReserva(r.estadoReserva());
                    reserva.setPersona(persona);
                    if (reserva.getMesasReservadas().isEmpty()) {
                        reserva.getMesasReservadas().add(new MesaReserva(null, mesa, reserva));
                    } else {
                        reserva.getMesasReservadas().getFirst().setMesa(mesa);
                    }

                    return convertToDTO(
                            reservaRepository.save(reserva)
                    );
                });
    }

    @Override
    @Transactional
    public boolean eliminar(Long id) {

        return reservaRepository.findById(id).map(reserva -> {
            for (DetalleReserva detalle : reserva.getDetalles()) {
                Producto producto = detalle.getProducto();
                producto.setStock(producto.getStock() + detalle.getCantidad());
                productoRepository.save(producto);
            }
            reservaRepository.delete(reserva);
            return true;
        }).orElse(false);
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
                r.getMesasReservadas().isEmpty() ? null
                        : r.getMesasReservadas().getFirst().getMesa().getIdMesa()
        );
    }
}
