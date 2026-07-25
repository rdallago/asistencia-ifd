package com.instituto.asistencia.dto;

public class EstadisticasTurnoDTO {

    private String turno;

    private long docentesAsignados;

    private long presentes;

    private long sinEgreso;

    private long sinFirmaTotal;

    private double porcentajeAsistencia;

    public EstadisticasTurnoDTO() {
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public long getDocentesAsignados() {
        return docentesAsignados;
    }

    public void setDocentesAsignados(long docentesAsignados) {
        this.docentesAsignados = docentesAsignados;
    }

    public long getPresentes() {
        return presentes;
    }

    public void setPresentes(long presentes) {
        this.presentes = presentes;
    }

    public long getSinEgreso() {
        return sinEgreso;
    }

    public void setSinEgreso(long sinEgreso) {
        this.sinEgreso = sinEgreso;
    }

    public long getSinFirmaTotal() {
        return sinFirmaTotal;
    }

    public void setSinFirmaTotal(long sinFirmaTotal) {
        this.sinFirmaTotal = sinFirmaTotal;
    }

    public double getPorcentajeAsistencia() {
        return porcentajeAsistencia;
    }

    public void setPorcentajeAsistencia(double porcentajeAsistencia) {
        this.porcentajeAsistencia = porcentajeAsistencia;
    }
}