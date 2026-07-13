package com.instituto.asistencia.dto;

import java.time.LocalDate;

public class EvolucionAsistenciaDTO {

    private LocalDate fecha;

private long presentes;

private long sinEgreso;

private long sinFirmaTotal;

    public EvolucionAsistenciaDTO() {
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
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

   

}