package com.web.proyect.hacienda_vimalu.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "reserva")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReserva;
    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(name = "fecha_creacion", updatable = false, columnDefinition = "DATE")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_reserva")
    private LocalDate fechaReserva;

    @Column(name = "hora_reserva", columnDefinition = "DATE")
    private LocalDateTime horaReserva;

    @Column(name = "cantidad")
    private Integer cantPersonas;

    @Column(length = 100)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoReserva estadoReserva;

    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    @ManyToOne
    @JoinColumn(name = "id_persona")
    private Persona persona;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL)
    private List<DetalleReserva> detalles = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "id_salon", nullable = false)
    private Salon salon;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Reserva() {
    }

    public Reserva(Long idReserva, LocalDateTime fechaCreacion, LocalDate fechaReserva, LocalDateTime horaReserva, Integer cantPersonas, String motivo, EstadoReserva estadoReserva, BigDecimal total, Persona persona, List<DetalleReserva> detalles, Salon salon) {
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
        this.salon = salon;
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

    public LocalDateTime getHoraReserva() {
        return horaReserva;
    }

    public void setHoraReserva(LocalDateTime horaReserva) {
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

    public Salon getSalon() { return salon; }

    public void setSalon(Salon salon) {
        this.salon = salon;
    }
}
