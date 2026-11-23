package com.web.proyect.hacienda_vimalu.repository;

import com.web.proyect.hacienda_vimalu.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Producto p where p.idProducto = :id")
    Optional<Producto> buscarParaActualizar(Long id);
}
