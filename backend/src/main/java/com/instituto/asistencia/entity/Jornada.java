package com.instituto.asistencia.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "jornadas",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "fecha",
                                "turno_id"
                        }
                )
        }
)
public class Jornada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @ManyToOne
    @JoinColumn(
            name = "turno_id",
            nullable = false
    )
    private Turno turno;

    @Column(nullable = false)
    private Boolean habilitada = false;

    @Column(nullable = false)
    private Boolean cerrada = false;

    @Column(name = "fecha_apertura")
    private LocalDateTime fechaApertura;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "cierre_automatico")
private Boolean cierreAutomatico = false;

    public Boolean getCierreAutomatico() {
        return cierreAutomatico;
    }

    public void setCierreAutomatico(Boolean cierreAutomatico) {
        this.cierreAutomatico = cierreAutomatico;
    }
    
    @ManyToOne
@JoinColumn(name = "usuario_apertura_id")
@JsonIgnoreProperties({
    "docente",
    "password",
    "hibernateLazyInitializer",
    "handler"
})
private Usuario usuarioApertura;

@ManyToOne
@JoinColumn(name = "usuario_cierre_id")
@JsonIgnoreProperties({
    "docente",
    "password",
    "hibernateLazyInitializer",
    "handler"
})
private Usuario usuarioCierre;

    @Column(length = 500)
    private String observaciones;

    public Jornada() {
    }

    // ==========================
    // GETTERS Y SETTERS
    // ==========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public Boolean getHabilitada() {
        return habilitada;
    }

    public void setHabilitada(Boolean habilitada) {
        this.habilitada = habilitada;
    }

    public Boolean getCerrada() {
        return cerrada;
    }

    public void setCerrada(Boolean cerrada) {
        this.cerrada = cerrada;
    }

    public LocalDateTime getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(LocalDateTime fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public Usuario getUsuarioApertura() {
        return usuarioApertura;
    }

    public void setUsuarioApertura(Usuario usuarioApertura) {
        this.usuarioApertura = usuarioApertura;
    }

    public Usuario getUsuarioCierre() {
        return usuarioCierre;
    }

    public void setUsuarioCierre(Usuario usuarioCierre) {
        this.usuarioCierre = usuarioCierre;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}