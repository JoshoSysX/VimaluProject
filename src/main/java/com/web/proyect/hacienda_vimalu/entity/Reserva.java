package com.web.proyect.hacienda_vimalu.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReserva;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_reserva")
    private LocalDate fechaReserva;

    @Column(name = "hora_reserva")
    private String horaReserva;

    @Column(name = "cant_personas")
    private Integer cantPersonas;

    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estadoReserva;

    private BigDecimal total;

    @ManyToOne
    @JoinColumn(name = "id_persona")
    private Persona persona;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL)
    private List<DetalleReserva> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MesaReserva> mesasReservadas = new ArrayList<>();

    public Reserva() {
    }

    public Reserva(Long idReserva, LocalDateTime fechaCreacion, LocalDate fechaReserva, String horaReserva, Integer cantPersonas, String motivo, EstadoReserva estadoReserva, BigDecimal total, Persona persona, List<DetalleReserva> detalles, List<MesaReserva> mesasReservadas) {
        this.idReserva = idReserva;
        this.fechaCreacion = fechaCreacion;
        this.fechaReserva = fechaReserva;
        this.horaReserva = horaReserva;
        this.cantPersonas = cantPersonas;
        this.motivo = motivo;
        this.estadoReserva = estadoReserva;
        this.total = total;
        this.persona = persona;
        this.detalles = detalles;
        this.mesasReservadas = mesasReservadas;
    }

    public Long getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Long idReserva) {
        this.idReserva = idReserva;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public String getHoraReserva() {
        return horaReserva;
    }

    public void setHoraReserva(String horaReserva) {
        this.horaReserva = horaReserva;
    }

    public Integer getCantPersonas() {
        return cantPersonas;
    }

    public void setCantPersonas(Integer cantPersonas) {
        this.cantPersonas = cantPersonas;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public EstadoReserva getEstadoReserva() {
        return estadoReserva;
    }

    public void setEstadoReserva(EstadoReserva estadoReserva) {
        this.estadoReserva = estadoReserva;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public List<DetalleReserva> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleReserva> detalles) {
        this.detalles = detalles;
    }

    public List<MesaReserva> getMesasReservadas() {
        return mesasReservadas;
    }

    public void setMesasReservadas(List<MesaReserva> mesasReservadas) {
        this.mesasReservadas = mesasReservadas;
    }
}
