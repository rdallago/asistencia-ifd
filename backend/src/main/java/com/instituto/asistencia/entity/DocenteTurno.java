package com.instituto.asistencia.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "docente_turnos")
public class DocenteTurno {

    @EmbeddedId
    private DocenteTurnoId id;

    @ManyToOne
    @MapsId("docenteId")
    @JoinColumn(name = "docente_id")
    private Docente docente;

    @ManyToOne
    @MapsId("turnoId")
    @JoinColumn(name = "turno_id")
    private Turno turno;

    public DocenteTurno() {
    }

    public DocenteTurnoId getId() {
        return id;
    }

    public void setId(DocenteTurnoId id) {
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
}