package com.instituto.asistencia.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DocenteTurnoId implements Serializable {

    private Long docenteId;

    private Long turnoId;

    public DocenteTurnoId() {
    }

    public DocenteTurnoId(
            Long docenteId,
            Long turnoId) {

        this.docenteId = docenteId;
        this.turnoId = turnoId;
    }

    public Long getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(Long docenteId) {
        this.docenteId = docenteId;
    }

    public Long getTurnoId() {
        return turnoId;
    }

    public void setTurnoId(Long turnoId) {
        this.turnoId = turnoId;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof DocenteTurnoId)) {
            return false;
        }

        DocenteTurnoId that =
                (DocenteTurnoId) o;

        return Objects.equals(
                docenteId,
                that.docenteId)
                &&
                Objects.equals(
                        turnoId,
                        that.turnoId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(
                docenteId,
                turnoId);
    }
}