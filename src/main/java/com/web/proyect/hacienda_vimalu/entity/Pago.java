package com.web.proyect.hacienda_vimalu.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "pago")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPago;

    @OneToOne
    @JoinColumn(name = "id_reserva")
    private Reserva reserva;

    private String comprobante;

    public Pago() {
    }

    public Pago(Long idPago, Reserva reserva, String comprobante) {
        this.idPago = idPago;
        this.reserva = reserva;
        this.comprobante = comprobante;
    }

    public Long getIdPago() {
        return idPago;
    }

    public void setIdPago(Long idPago) {
        this.idPago = idPago;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }
}
