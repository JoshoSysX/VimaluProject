package com.web.proyect.hacienda_vimalu.repository;

import com.web.proyect.hacienda_vimalu.entity.MesaReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface MesaReservaRepository extends JpaRepository<MesaReserva, Long> {
    List<MesaReserva> findByReserva_IdReserva(Long idReserva);
    List<MesaReserva> findByMesa_IdMesa(Long idMesa);
}
