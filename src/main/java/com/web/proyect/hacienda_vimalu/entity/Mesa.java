package com.web.proyect.hacienda_vimalu.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "mesa")
public class Mesa {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mesa")
    private Long idMesa;
    @ManyToOne @JoinColumn(name = "id_salon", nullable = false)
    private Salon salon;
    @Column(nullable = false, length = 50)
    private String nombre;
    @Column(nullable = false)
    private Integer capacidad;
    public Mesa() {}
    public Long getIdMesa() { return idMesa; }
    public void setIdMesa(Long idMesa) { this.idMesa = idMesa; }
    public Salon getSalon() { return salon; }
    public void setSalon(Salon salon) { this.salon = salon; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }
}
