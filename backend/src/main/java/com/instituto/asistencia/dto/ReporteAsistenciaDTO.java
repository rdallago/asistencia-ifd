package com.instituto.asistencia.dto;

import java.time.LocalTime;
import com.instituto.asistencia.entity.Asistencia;
public class ReporteAsistenciaDTO {

    private String dni;

    private String docente;

    private String turno;

    private LocalTime ingreso;

    private LocalTime egreso;

    private String estado;

    public ReporteAsistenciaDTO() {
    }

    public ReporteAsistenciaDTO(
            String dni,
            String docente,
            String turno,
            LocalTime ingreso,
            LocalTime egreso,
            String estado) {

        this.dni = dni;
        this.docente = docente;
        this.turno = turno;
        this.ingreso = ingreso;
        this.egreso = egreso;
        this.estado = estado;
    }
    public ReporteAsistenciaDTO(
        Asistencia a) {

    this.dni =
            a.getDocente().getDni();

    this.docente =
            a.getDocente().getApellido()
            + ", "
            + a.getDocente().getNombre();

    this.turno =
            a.getTurno().getNombre();

    this.ingreso =
            a.getHoraIngreso();

    this.egreso =
            a.getHoraEgreso();

    this.estado =
            a.getEstado().name();
}

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getDocente() {
        return docente;
    }

    public void setDocente(String docente) {
        this.docente = docente;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public LocalTime getIngreso() {
        return ingreso;
    }

    public void setIngreso(LocalTime ingreso) {
        this.ingreso = ingreso;
    }

    public LocalTime getEgreso() {
        return egreso;
    }

    public void setEgreso(LocalTime egreso) {
        this.egreso = egreso;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}