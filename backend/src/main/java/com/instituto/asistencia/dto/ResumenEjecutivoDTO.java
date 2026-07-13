package com.instituto.asistencia.dto;

import java.util.ArrayList;
import java.util.List;

public class ResumenEjecutivoDTO {

    // Mantener temporalmente (compatibilidad)
    private List<String> conclusiones = new ArrayList<>();

    // Nuevo modelo BI
    private String estadoGeneral;

    private List<String> tendencias = new ArrayList<>();

    private List<String> observaciones = new ArrayList<>();

    private List<String> recomendaciones = new ArrayList<>();

    // ==========================
    // Compatibilidad
    // ==========================

    public List<String> getConclusiones() {
        return conclusiones;
    }

    public void setConclusiones(List<String> conclusiones) {
        this.conclusiones = conclusiones;
    }

    // ==========================
    // Nuevo BI
    // ==========================

    public String getEstadoGeneral() {
        return estadoGeneral;
    }

    public void setEstadoGeneral(String estadoGeneral) {
        this.estadoGeneral = estadoGeneral;
    }

    public List<String> getTendencias() {
        return tendencias;
    }

    public void setTendencias(List<String> tendencias) {
        this.tendencias = tendencias;
    }

    public List<String> getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(List<String> observaciones) {
        this.observaciones = observaciones;
    }

    public List<String> getRecomendaciones() {
        return recomendaciones;
    }

    public void setRecomendaciones(List<String> recomendaciones) {
        this.recomendaciones = recomendaciones;
    }
}