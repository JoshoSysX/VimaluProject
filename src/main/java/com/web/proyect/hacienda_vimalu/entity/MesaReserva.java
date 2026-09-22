package com.web.proyect.hacienda_vimalu.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "mesa_reserva")
public class MesaReserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMesaReserva;

    @ManyToOne
    @JoinColumn(name = "id_mesa", nullable = false)
    private Mesa mesa;

    @ManyToOne
    @JoinColumn(name = "id_reserva", nullable = false)
    private Reserva reserva;

    public MesaReserva() {
    }

    public MesaReserva(Long idMesaReserva, Mesa mesa, Reserva reserva) {
        this.idMesaReserva = idMesaReserva;
        this.mesa = mesa;
        this.reserva = reserva;
    }

    public Long getIdMesaReserva() {
        return idMesaReserva;
    }

    public void setIdMesaReserva(Long idMesaReserva) {
        this.idMesaReserva = idMesaReserva;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }
}
