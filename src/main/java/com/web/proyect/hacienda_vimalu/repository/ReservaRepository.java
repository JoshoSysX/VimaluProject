package com.web.proyect.hacienda_vimalu.repository;
import com.web.proyect.hacienda_vimalu.entity.Reserva;
import com.web.proyect.hacienda_vimalu.entity.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    boolean existsByMesa_IdMesa(Long idMesa);
    List<Reserva> findByMesa_IdMesaAndFechaReservaAndEstadoReservaIn(Long idMesa, LocalDate fecha, List<EstadoReserva> estados);
    List<Reserva> findByMesa_IdMesaAndEstadoReservaIn(Long idMesa, List<EstadoReserva> estados);
    List<Reserva> findByMesa_Salon_IdSalonAndFechaReservaAndEstadoReservaIn(Long idSalon, LocalDate fecha, List<EstadoReserva> estados);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Reserva r where r.idReserva = :id")
    Optional<Reserva> buscarParaActualizar(Long id);
}
