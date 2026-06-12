package com.instituto.asistencia.repository;

import com.instituto.asistencia.entity.Asistencia;
import com.instituto.asistencia.entity.enums.EstadoAsistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AsistenciaRepository
        extends JpaRepository<Asistencia, Long> {

    Optional<Asistencia>
    findByDocenteIdAndTurnoIdAndFecha(
            Long docenteId,
            Long turnoId,
            LocalDate fecha
    );

    List<Asistencia>
    findByFecha(
            LocalDate fecha
    );

    List<Asistencia>
    findByFechaAndEstado(
            LocalDate fecha,
            EstadoAsistencia estado
    );

    List<Asistencia>
    findByDocenteId(
            Long docenteId
    );

    boolean existsByDocenteIdAndTurnoIdAndFecha(
            Long docenteId,
            Long turnoId,
            LocalDate fecha
    );
    
    Optional<Asistencia>
findByDocenteIdAndFecha(
        Long docenteId,
        LocalDate fecha
);
}