package com.instituto.asistencia.dto;

public class RankingAusenciasDTO {

    private Long docenteId;
    private String nombre;
    private String apellido;
    private Long cantidadAusencias;

    public RankingAusenciasDTO() {
    }

    public RankingAusenciasDTO(
            Long docenteId,
            String nombre,
            String apellido,
            Long cantidadAusencias) {

        this.docenteId = docenteId;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cantidadAusencias = cantidadAusencias;
    }

    public Long getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(Long docenteId) {
        this.docenteId = docenteId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Long getCantidadAusencias() {
        return cantidadAusencias;
    }

    public void setCantidadAusencias(Long cantidadAusencias) {
        this.cantidadAusencias = cantidadAusencias;
    }
}