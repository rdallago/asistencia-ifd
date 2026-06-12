package com.instituto.asistencia.entity;

import com.instituto.asistencia.entity.enums.EstadoAsistencia;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
        name = "asistencias",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "docente_id",
                                "turno_id",
                                "fecha"
                        }
                )

        }
        
        
)
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(
            name = "docente_id",
            nullable = false
    )
    private Docente docente;
    
    @ManyToOne
    @JoinColumn(
        name = "jornada_id",
        nullable = false
)
private Jornada jornada;

    @ManyToOne
    @JoinColumn(
            name = "turno_id",
            nullable = false
    )
    private Turno turno;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_ingreso")
    private LocalTime horaIngreso;

    @Column(name = "hora_egreso")
    private LocalTime horaEgreso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAsistencia estado;

    @Column(length = 500)
    private String observaciones;

    @Column(
            name = "fecha_creacion",
            nullable = false
    )
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    public Asistencia() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Docente getDocente() {
        return docente;
    }

    public void setDocente(Docente docente) {
        this.docente = docente;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraIngreso() {
        return horaIngreso;
    }

    public void setHoraIngreso(LocalTime horaIngreso) {
        this.horaIngreso = horaIngreso;
    }

    public LocalTime getHoraEgreso() {
        return horaEgreso;
    }

    public void setHoraEgreso(LocalTime horaEgreso) {
        this.horaEgreso = horaEgreso;
    }

    public EstadoAsistencia getEstado() {
        return estado;
    }

    public void setEstado(EstadoAsistencia estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public Jornada getJornada() {
        return jornada;
    }

    public void setJornada(Jornada jornada) {
        this.jornada = jornada;
    }

    public void setFechaModificacion(
            LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }
}