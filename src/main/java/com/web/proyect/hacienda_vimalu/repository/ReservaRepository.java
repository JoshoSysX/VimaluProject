package com.web.proyect.hacienda_vimalu.repository;

import com.web.proyect.hacienda_vimalu.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
}
