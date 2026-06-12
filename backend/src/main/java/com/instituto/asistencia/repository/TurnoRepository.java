package com.instituto.asistencia.repository;

import com.instituto.asistencia.entity.Turno;
import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TurnoRepository
        extends JpaRepository<Turno, Long> {

    List<Turno> findByActivoTrue();
    Optional<Turno> findFirstByActivoTrue();

}