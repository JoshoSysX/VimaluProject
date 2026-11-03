package com.web.proyect.hacienda_vimalu.repository;
import com.web.proyect.hacienda_vimalu.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByReserva_IdReserva(Long idReserva);
}