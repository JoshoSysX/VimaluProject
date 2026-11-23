package com.web.proyect.hacienda_vimalu.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "salon")
public class Salon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_salon")
    private Long idSalon;


    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(name = "cantidad_mesas", nullable = false)
    private Integer cantidadMesas;

    @Column(nullable = false, length = 20)
    private String estado;
    public Salon() {}
    public Long getIdSalon() {
        return idSalon;
    }
    public void setIdSalon(Long idSalon) {
        this.idSalon = idSalon;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public Integer getCantidadMesas() {
        return cantidadMesas;
    }
    public void setCantidadMesas(Integer cantidadMesas) {
        this.cantidadMesas = cantidadMesas;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
}