package com.web.proyect.hacienda_vimalu.repository;
import com.web.proyect.hacienda_vimalu.entity.Mesa;
import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
public interface MesaRepository extends JpaRepository<Mesa, Long> {
    List<Mesa> findBySalon_IdSalon(Long idSalon);
    long countBySalon_IdSalon(Long idSalon);
    boolean existsBySalon_IdSalon(Long idSalon);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Mesa m where m.idMesa = :id")
    Optional<Mesa> buscarParaActualizar(Long id);
}
