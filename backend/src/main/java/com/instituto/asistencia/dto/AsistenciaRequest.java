package com.instituto.asistencia.dto;

public class AsistenciaRequest {

    private String dni;

    private Long jornadaId;

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public Long getJornadaId() {
        return jornadaId;
    }

    public void setJornadaId(Long jornadaId) {
        this.jornadaId = jornadaId;
    }
}