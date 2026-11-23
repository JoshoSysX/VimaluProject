package com.web.proyect.hacienda_vimalu.services;

import java.util.Objects;
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
import com.web.proyect.hacienda_vimalu.entity.Mesa;
import com.web.proyect.hacienda_vimalu.entity.Persona;
import com.web.proyect.hacienda_vimalu.entity.Reserva;
import com.web.proyect.hacienda_vimalu.repository.SalonRepository;
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
    private final SalonRepository salonRepository;
    private final MesaRepository mesaRepository;
    private final ProductoRepository productoRepository;
    private final PagoRepository pagoRepository;
    private static final BigDecimal COSTO_RESERVA = new BigDecimal("25.00");

    public ReservaServiceImpl(
            ReservaRepository reservaRepository,
            PersonaRepository personaRepository,
            SalonRepository salonRepository, MesaRepository mesaRepository,
            ProductoRepository productoRepository, PagoRepository pagoRepository) {

        this.reservaRepository = reservaRepository;
        this.personaRepository = personaRepository;
        this.salonRepository = salonRepository;
        this.mesaRepository = mesaRepository;
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

        Mesa mesa = mesaRepository.buscarParaActualizar(r.idMesa())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Mesa no encontrada"));

        validarDisponibilidad(mesa, r.fechaReserva(), r.cantidadPersonas(), null);
        Reserva reserva = new Reserva();


        reserva.setFechaReserva(r.fechaReserva());
        reserva.setHoraReserva(r.horaReserva());
        reserva.setCantidadPersonas(r.cantidadPersonas());
        reserva.setMotivo(r.motivo());
        reserva.setFechaCreacion(LocalDateTime.now());
        reserva.setEstadoReserva(EstadoReserva.PENDIENTE);
        reserva.setPersona(persona);
        reserva.setMesa(mesa);

        BigDecimal total = BigDecimal.ZERO;
        // El mismo orden evita bloqueos cruzados cuando dos reservas piden varios productos.
        List<ItemReservaDTO> items = r.items() == null ? List.of() : r.items().stream()
                .sorted(java.util.Comparator.comparing(ItemReservaDTO::idProducto)).toList();
        for (ItemReservaDTO item : items) {
            Producto producto = productoRepository.buscarParaActualizar(item.idProducto())
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

                    Mesa mesa = mesaRepository.buscarParaActualizar(r.idMesa())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Mesa no encontrada"));

                    if (r.estadoReserva() == EstadoReserva.PENDIENTE || r.estadoReserva() == EstadoReserva.CONFIRMADO) {
                        validarDisponibilidad(mesa, r.fechaReserva(), r.cantidadPersonas(), id);
                    }
                    reserva.setFechaReserva(r.fechaReserva());
                    reserva.setHoraReserva(r.horaReserva());
                    reserva.setCantidadPersonas(r.cantidadPersonas());
                    reserva.setMotivo(r.motivo());
                    reserva.setEstadoReserva(r.estadoReserva());
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

    private void validarDisponibilidad(Mesa mesa, LocalDate fecha, int personas, Long idActual) {
        var salon = salonRepository.buscarParaActualizar(mesa.getSalon().getIdSalon())
                .orElseThrow(() -> new ResourceNotFoundException("Salón no encontrado"));
        if (!"DISPONIBLE".equals(salon.getEstado())) {
            throw new ReglaNegocioException("El salón no está disponible");
        }
        if (personas > mesa.getCapacidad()) {
            throw new ReglaNegocioException("La mesa permite hasta " + mesa.getCapacidad()
                    + " personas; comuníquese con un administrador");
        }
        boolean ocupada = reservaRepository.findByMesa_IdMesaAndFechaReservaAndEstadoReservaIn(
                mesa.getIdMesa(), fecha, List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADO))
                .stream().anyMatch(r -> !Objects.equals(r.getIdReserva(), idActual));
        if (ocupada) throw new ReglaNegocioException("La mesa ya está reservada para esa fecha");
    }
    private ReservaDTO convertToDTO(Reserva r) {
        BigDecimal pagado = BigDecimal.ZERO;
        for (var pago : pagoRepository.findByReserva_IdReserva(r.getIdReserva())) {
            if ("PAGADO".equals(pago.getEstado())) pagado = pagado.add(pago.getMonto());
        }

        return new ReservaDTO(
                r.getIdReserva(),
                r.getFechaReserva(),
                r.getHoraReserva(),
                r.getCantidadPersonas(),
                r.getMotivo(),
                r.getEstadoReserva(),
                r.getTotal(),
                r.getPersona().getIdPersona(),
                r.getMesa().getIdMesa(), pagado, r.getTotal().subtract(pagado).max(BigDecimal.ZERO)
        );
    }
}
