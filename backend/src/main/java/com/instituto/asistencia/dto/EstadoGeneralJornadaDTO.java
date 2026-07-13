package com.instituto.asistencia.dto;

public class EstadoGeneralJornadaDTO {

    private int puntaje;

    private String estado;

    private String descripcion;
    
    private long registrosCompletos;

    private long registrosIniciados;

    public long getRegistrosCompletos() {
        return registrosCompletos;
    }

    public void setRegistrosCompletos(long registrosCompletos) {
        this.registrosCompletos = registrosCompletos;
    }

    public long getRegistrosIniciados() {
        return registrosIniciados;
    }

    public void setRegistrosIniciados(long registrosIniciados) {
        this.registrosIniciados = registrosIniciados;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}