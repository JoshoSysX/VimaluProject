package com.web.proyect.hacienda_vimalu.services;

import com.web.proyect.hacienda_vimalu.dto.DetalleReservaDTO;
import com.web.proyect.hacienda_vimalu.entity.DetalleReserva;
import com.web.proyect.hacienda_vimalu.entity.Producto;
import com.web.proyect.hacienda_vimalu.entity.Reserva;
import com.web.proyect.hacienda_vimalu.repository.DetalleReservaRepository;
import com.web.proyect.hacienda_vimalu.repository.ProductoRepository;
import com.web.proyect.hacienda_vimalu.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DetalleReservaServiceImpl
        implements IDetalleReservaService {

    private final DetalleReservaRepository detalleRepository;
    private final ReservaRepository reservaRepository;
    private final ProductoRepository productoRepository;

    public DetalleReservaServiceImpl(
            DetalleReservaRepository detalleRepository,
            ReservaRepository reservaRepository,
            ProductoRepository productoRepository) {

        this.detalleRepository = detalleRepository;
        this.reservaRepository = reservaRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetalleReservaDTO> listarTodo() {

        return detalleRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DetalleReservaDTO> buscarPorId(Long id) {

        return detalleRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public DetalleReservaDTO crear(DetalleReservaDTO d) {

        Reserva reserva = reservaRepository.findById(d.idReserva())
                .orElseThrow(() ->
                        new RuntimeException("Reserva no encontrada"));

        Producto producto = productoRepository.findById(d.idProducto())
                .orElseThrow(() ->
                        new RuntimeException("Producto no encontrado"));

        DetalleReserva detalle = new DetalleReserva();

        detalle.setReserva(reserva);
        detalle.setProducto(producto);
        detalle.setCantidad(d.cantidad());
        detalle.setPrecioCompra(d.precioCompra());
        detalle.setSubtotal(d.subtotal());

        return convertToDTO(
                detalleRepository.save(detalle)
        );
    }

    @Override
    @Transactional
    public Optional<DetalleReservaDTO> actualizar(
            Long id,
            DetalleReservaDTO d) {

        return detalleRepository.findById(id)
                .map(detalle -> {

                    Reserva reserva = reservaRepository
                            .findById(d.idReserva())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Reserva no encontrada"));

                    Producto producto = productoRepository
                            .findById(d.idProducto())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Producto no encontrado"));

                    detalle.setReserva(reserva);
                    detalle.setProducto(producto);
                    detalle.setCantidad(d.cantidad());
                    detalle.setPrecioCompra(d.precioCompra());
                    detalle.setSubtotal(d.subtotal());

                    return convertToDTO(
                            detalleRepository.save(detalle)
                    );
                });
    }

    @Override
    @Transactional
    public boolean eliminar(Long id) {

        if (detalleRepository.existsById(id)) {
            detalleRepository.deleteById(id);
            return true;
        }

        return false;
    }

    private DetalleReservaDTO convertToDTO(
            DetalleReserva d) {

        return new DetalleReservaDTO(
                d.getIdDetalle(),
                d.getReserva().getIdReserva(),
                d.getProducto().getIdProducto(),
                d.getCantidad(),
                d.getPrecioCompra(),
                d.getSubtotal()
        );
    }
}
