package com.instituto.asistencia.dto;



public class EstadisticasDTO {
    
    private long docentesRegistrados;

    private long docentesActivos;

    private long presentes;

    private long sinEgreso;

    private long sinFirmaTotal;

    private long totalDocentes;
    
       private long jornadasAbiertas;

    private long turnosConfigurados;

    private long asignacionesVigentes;

    private Double porcentajeAsistencia;
    
    private long actividadRegistrada;

    public long getActividadRegistrada() {
        return actividadRegistrada;
    }

    public void setActividadRegistrada(long actividadRegistrada) {
        this.actividadRegistrada = actividadRegistrada;
    }

    public EstadisticasDTO() {
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

    public long getTotalDocentes() {
        return totalDocentes;
    }

    public void setTotalDocentes(long totalDocentes) {
        this.totalDocentes = totalDocentes;
    }

    public long getDocentesRegistrados() {
        return docentesRegistrados;
    }

    public void setDocentesRegistrados(long docentesRegistrados) {
        this.docentesRegistrados = docentesRegistrados;
    }

    public long getDocentesActivos() {
        return docentesActivos;
    }

    public void setDocentesActivos(long docentesActivos) {
        this.docentesActivos = docentesActivos;
    }

    public long getJornadasAbiertas() {
        return jornadasAbiertas;
    }

    public void setJornadasAbiertas(long jornadasAbiertas) {
        this.jornadasAbiertas = jornadasAbiertas;
    }

    public long getTurnosConfigurados() {
        return turnosConfigurados;
    }

    public void setTurnosConfigurados(long turnosConfigurados) {
        this.turnosConfigurados = turnosConfigurados;
    }

    public long getAsignacionesVigentes() {
        return asignacionesVigentes;
    }

    public void setAsignacionesVigentes(long asignacionesVigentes) {
        this.asignacionesVigentes = asignacionesVigentes;
    }

    public Double getPorcentajeAsistencia() {
        return porcentajeAsistencia;
    }

    public void setPorcentajeAsistencia(Double porcentajeAsistencia) {
        this.porcentajeAsistencia = porcentajeAsistencia;
    }
 
}