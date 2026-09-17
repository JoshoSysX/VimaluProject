package com.web.proyect.hacienda_vimalu.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "mesa")
public class Mesa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMesa;

    private Integer capacidad;

    @Column(name = "nro_mesa")
    private Integer nroMesa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMesa estadoMesa;

    public Mesa() {
    }

    public Mesa(Long idMesa, Integer capacidad, Integer nroMesa, EstadoMesa estadoMesa) {
        this.idMesa = idMesa;
        this.capacidad = capacidad;
        this.nroMesa = nroMesa;
        this.estadoMesa = estadoMesa;
    }

    public Long getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(Long idMesa) {
        this.idMesa = idMesa;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public Integer getNroMesa() {
        return nroMesa;
    }

    public void setNroMesa(Integer nroMesa) {
        this.nroMesa = nroMesa;
    }

    public EstadoMesa getEstadoMesa() {
        return estadoMesa;
    }

    public void setEstadoMesa(EstadoMesa estadoMesa) {
        this.estadoMesa = estadoMesa;
    }
}
