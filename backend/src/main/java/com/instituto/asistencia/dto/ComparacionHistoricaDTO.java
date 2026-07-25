package com.instituto.asistencia.dto;

public class ComparacionHistoricaDTO {

    private double promedioHistorico;

    private double valorActual;

    private double diferencia;

    private String tendencia;

    public double getPromedioHistorico() {
        return promedioHistorico;
    }

    public void setPromedioHistorico(double promedioHistorico) {
        this.promedioHistorico = promedioHistorico;
    }

    public double getValorActual() {
        return valorActual;
    }

    public void setValorActual(double valorActual) {
        this.valorActual = valorActual;
    }

    public double getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(double diferencia) {
        this.diferencia = diferencia;
    }

    public String getTendencia() {
        return tendencia;
    }

    public void setTendencia(String tendencia) {
        this.tendencia = tendencia;
    }

}