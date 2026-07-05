package com.instituto.asistencia.dto;

public class IndicadoresInstitucionalesDTO {

    private Long docentesRegistrados;

    private Long docentesActivos;

    private Long turnosConfigurados;

    private Long asignacionesVigentes;

    private Long jornadasAbiertas;

    public Long getDocentesRegistrados() {
        return docentesRegistrados;
    }

    public void setDocentesRegistrados(Long docentesRegistrados) {
        this.docentesRegistrados = docentesRegistrados;
    }

    public Long getDocentesActivos() {
        return docentesActivos;
    }

    public void setDocentesActivos(Long docentesActivos) {
        this.docentesActivos = docentesActivos;
    }

    public Long getTurnosConfigurados() {
        return turnosConfigurados;
    }

    public void setTurnosConfigurados(Long turnosConfigurados) {
        this.turnosConfigurados = turnosConfigurados;
    }

    public Long getAsignacionesVigentes() {
        return asignacionesVigentes;
    }

    public void setAsignacionesVigentes(Long asignacionesVigentes) {
        this.asignacionesVigentes = asignacionesVigentes;
    }

    public Long getJornadasAbiertas() {
        return jornadasAbiertas;
    }

    public void setJornadasAbiertas(Long jornadasAbiertas) {
        this.jornadasAbiertas = jornadasAbiertas;
    }

}