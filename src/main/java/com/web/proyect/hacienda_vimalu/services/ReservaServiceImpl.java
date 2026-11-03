package com.web.proyect.hacienda_vimalu.services;

import java.util.Objects;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;
import java.time.LocalDate;
import com.web.proyect.hacienda_vimalu.exception.ReglaNegocioException;
import com.web.proyect.hacienda_vimalu.entity.Pago;
import com.web.proyect.hacienda_vimalu.repository.PagoRepository;
import com.web.proyect.hacienda_vimalu.dto.ReservaDTO;
import com.web.proyect.hacienda_vimalu.dto.DetalleReservaDTO;
import com.web.proyect.hacienda_vimalu.dto.ItemReservaDTO;
import com.web.proyect.hacienda_vimalu.dto.ReservaResponseDTO;
import com.web.proyect.hacienda_vimalu.dto.SolicitudReservaDTO;
import com.web.proyect.hacienda_vimalu.entity.DetalleReserva;
import com.web.proyect.hacienda_vimalu.entity.EstadoReserva;
import com.web.proyect.hacienda_vimalu.entity.Producto;
import com.web.proyect.hacienda_vimalu.exception.ResourceNotFoundException;
import com.web.proyect.hacienda_vimalu.exception.InsufficientStockException;
import com.web.proyect.hacienda_vimalu.entity.Salon;
import com.web.proyect.hacienda_vimalu.entity.Persona;
import com.web.proyect.hacienda_vimalu.entity.Reserva;
import com.web.proyect.hacienda_vimalu.repository.SalonRepository;
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
    private final SalonRepository salonRepository;
    private final ProductoRepository productoRepository;
    private final PagoRepository pagoRepository;
    private static final BigDecimal COSTO_RESERVA = new BigDecimal("25.00");

    public ReservaServiceImpl(
            ReservaRepository reservaRepository,
            PersonaRepository personaRepository,
            SalonRepository salonRepository,
            ProductoRepository productoRepository, PagoRepository pagoRepository) {

        this.reservaRepository = reservaRepository;
        this.personaRepository = personaRepository;
        this.salonRepository = salonRepository;
        this.productoRepository = productoRepository;
        this.pagoRepository = pagoRepository;
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

        Salon salon = salonRepository.buscarParaActualizar(r.idSalon())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Salón no encontrado"));

        validarDisponibilidad(salon, r.fechaReserva(), r.cantPersonas(), null);
        Reserva reserva = new Reserva();

        reserva.setFechaReserva(r.fechaReserva());
        reserva.setHoraReserva(r.fechaReserva().atTime(LocalTime.parse(r.horaReserva())));
        reserva.setNombre(r.nombre());
        reserva.setCantPersonas(r.cantPersonas());
        reserva.setMotivo(r.motivo());
        reserva.setFechaCreacion(LocalDateTime.now());
        reserva.setEstadoReserva(EstadoReserva.PENDIENTE);
        reserva.setPersona(persona);
        reserva.setSalon(salon);

        BigDecimal total = BigDecimal.ZERO;
        for (ItemReservaDTO item : r.items() == null ? List.<ItemReservaDTO>of() : r.items()) {
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
        reserva.setTotal(total.max(COSTO_RESERVA));
        Reserva guardada = reservaRepository.save(reserva);
        // El pago se genera pendiente; no se descuenta hasta confirmar que fue pagado.
        Pago pago = new Pago();
        pago.setReserva(guardada);
        pago.setMonto(COSTO_RESERVA);
        pago.setFechaPago(null);
        pago.setTipoPago("POR_DEFINIR");
        pago.setTipoComprobante("RESERVA");
        pago.setEstado("PENDIENTE");
        pagoRepository.save(pago);
        List<DetalleReservaDTO> detalles = guardada.getDetalles().stream()
                .map(d -> new DetalleReservaDTO(d.getIdDetalle(), guardada.getIdReserva(),
                        d.getProducto().getIdProducto(), d.getCantidad(), d.getPrecioCompra(), d.getSubtotal()))
                .toList();
        return new ReservaResponseDTO(convertToDTO(guardada), detalles);
    }

    @Override
    @Transactional
    public Optional<ReservaDTO> actualizar(Long id, ReservaDTO r) {

        return reservaRepository.buscarParaActualizar(id)
                .map(reserva -> {

                    Persona persona = personaRepository.findById(r.idPersona())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Persona no encontrada"));

                    Salon salon = salonRepository.buscarParaActualizar(r.idSalon())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Salón no encontrado"));

                    if (r.estadoReserva() == EstadoReserva.PENDIENTE || r.estadoReserva() == EstadoReserva.CONFIRMADO) {
                        validarDisponibilidad(salon, r.fechaReserva(), r.cantPersonas(), id);
                    }
                    reserva.setFechaReserva(r.fechaReserva());
                    reserva.setHoraReserva(r.fechaReserva().atTime(LocalTime.parse(r.horaReserva())));
                    reserva.setNombre(r.nombre());
                    reserva.setCantPersonas(r.cantPersonas());
                    reserva.setMotivo(r.motivo());
                    reserva.setEstadoReserva(r.estadoReserva());
                    reserva.setPersona(persona);
                    reserva.setSalon(salon);

                    return convertToDTO(
                            reservaRepository.save(reserva)
                    );
                });
    }

    @Override
    @Transactional
    public boolean eliminar(Long id) {

        return reservaRepository.buscarParaActualizar(id).map(reserva -> {
            var pagos = pagoRepository.findByReserva_IdReserva(id);
            if (pagos.stream().anyMatch(p -> "PAGADO".equals(p.getEstado()))) {
                throw new ReglaNegocioException("La reserva tiene pagos registrados; cambie su estado en lugar de eliminarla");
            }
            pagoRepository.deleteAll(pagos);
            for (DetalleReserva detalle : reserva.getDetalles()) {
                Producto producto = detalle.getProducto();
                producto.setStock(producto.getStock() + detalle.getCantidad());
                productoRepository.save(producto);
            }
            reservaRepository.delete(reserva);
            return true;
        }).orElse(false);
    }

    private void validarDisponibilidad(Salon salon, LocalDate fecha, int personas, Long idActual) {
        if (!"DISPONIBLE".equals(salon.getEstado())) {
            throw new ReglaNegocioException("El salón no está disponible");
        }
        if (personas > salon.getMesa().getCapacidad()) {
            throw new ReglaNegocioException(
                    "Para reservar para más de " + salon.getMesa().getCapacidad() + " personas, comuníquese con un administrador");
        }
        long ocupadas = reservaRepository.findBySalon_IdSalonAndFechaReservaAndEstadoReservaIn(
                salon.getIdSalon(), fecha, List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADO))
                .stream().filter(r -> !Objects.equals(r.getIdReserva(), idActual)).count();
        if (ocupadas >= salon.getCantidad()) {
            throw new ReglaNegocioException("No quedan mesas disponibles en el salón para esa fecha");
        }
    }

    private ReservaDTO convertToDTO(Reserva r) {
        BigDecimal pagado = BigDecimal.ZERO;
        for (var pago : pagoRepository.findByReserva_IdReserva(r.getIdReserva())) {
            if ("PAGADO".equals(pago.getEstado())) pagado = pagado.add(pago.getMonto());
        }

        return new ReservaDTO(
                r.getNombre(),
                r.getIdReserva(),
                r.getFechaReserva(),
                r.getHoraReserva().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                r.getCantPersonas(),
                r.getMotivo(),
                r.getEstadoReserva(),
                r.getTotal(),
                r.getPersona().getIdPersona(),
                r.getSalon().getIdSalon(), pagado, r.getTotal().subtract(pagado).max(BigDecimal.ZERO)
        );
    }
}
