package com.instituto.asistencia.repository;

import com.instituto.asistencia.entity.DocenteTurno;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DocenteTurnoRepository
        extends JpaRepository<DocenteTurno, Long> {

    List<DocenteTurno> findByTurno_Id(Long turnoId);

    List<DocenteTurno> findByDocente_Id(Long docenteId);

    boolean existsByDocente_IdAndTurno_Id(
            Long docenteId,
            Long turnoId
    );

    List<DocenteTurno> findByTurno_IdAndActivoTrue(
            Long turnoId
    );

    @Query("""
        SELECT dt
        FROM DocenteTurno dt
        WHERE dt.turno.id = :turnoId
        AND dt.activo = true
        AND dt.fechaDesde <= :fecha
        AND (dt.fechaHasta IS NULL OR dt.fechaHasta >= :fecha)
    """)
    List<DocenteTurno> findActivosPorTurnoYFecha(
            Long turnoId,
            LocalDate fecha
    );
    
    @Query("""
    SELECT dt
    FROM DocenteTurno dt
    WHERE dt.turno.id = :turnoId
    AND dt.fechaDesde <= :fecha
    AND (dt.fechaHasta IS NULL OR dt.fechaHasta >= :fecha)
""")
List<DocenteTurno> findVigentesPorTurnoYFecha(
        Long turnoId,
        LocalDate fecha
);
List<DocenteTurno> findByActivoTrue();

boolean existsByDocente_IdAndTurno_IdAndActivoTrue(
        Long docenteId,
        Long turnoId
);
long countByActivoTrue();



}