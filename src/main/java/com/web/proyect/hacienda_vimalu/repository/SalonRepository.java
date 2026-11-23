package com.web.proyect.hacienda_vimalu.repository;
import com.web.proyect.hacienda_vimalu.entity.Salon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
public interface SalonRepository extends JpaRepository<Salon, Long> {

    // Evita que dos reservas tomen la ultima mesa al mismo tiempo.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Salon s where s.idSalon = :id")
    Optional<Salon> buscarParaActualizar(Long id);
}