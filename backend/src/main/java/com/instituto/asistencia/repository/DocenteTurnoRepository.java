package com.instituto.asistencia.repository;

import com.instituto.asistencia.entity.DocenteTurno;
import com.instituto.asistencia.entity.DocenteTurnoId;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocenteTurnoRepository
        extends JpaRepository<
                DocenteTurno,
                DocenteTurnoId> {

    List<DocenteTurno>
    findByTurno_Id(Long turnoId);

    List<DocenteTurno>
    findByDocente_Id(Long docenteId);

    boolean existsByDocente_IdAndTurno_Id(
            Long docenteId,
            Long turnoId
    );
}