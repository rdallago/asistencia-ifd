    package com.instituto.asistencia.entity;
import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "docente_turnos")
public class DocenteTurno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;

    @ManyToOne
    @JoinColumn(name = "turno_id", nullable = false)
    private Turno turno;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_desde", nullable = false)
    private LocalDate fechaDesde;

    @Column(name = "fecha_hasta")
    private LocalDate fechaHasta;

 


    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    


    public DocenteTurno() {
    }

 public Long getId() {
    return id;
}

public void setId(Long id) {
    this.id = id;
}

    public Docente getDocente() {
        return docente;
    }

    public void setDocente(Docente docente) {
        this.docente = docente;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }
    public LocalDate getFechaDesde() {
    return fechaDesde;
}

public void setFechaDesde(LocalDate fechaDesde) {
    this.fechaDesde = fechaDesde;
}

public LocalDate getFechaHasta() {
    return fechaHasta;
}

public void setFechaHasta(LocalDate fechaHasta) {
    this.fechaHasta = fechaHasta;
}
}