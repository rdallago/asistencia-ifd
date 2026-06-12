package com.instituto.asistencia.repository;

import com.instituto.asistencia.entity.Jornada;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JornadaRepository
        extends JpaRepository<Jornada, Long> {

    Optional<Jornada> findByFechaAndTurnoId(
            LocalDate fecha,
            Long turnoId
    );

    boolean existsByFechaAndTurnoId(
            LocalDate fecha,
            Long turnoId
    );

    List<Jornada> findByFecha(
            LocalDate fecha
    );

    List<Jornada> findByCerradaFalse();

    List<Jornada> findByHabilitadaTrueAndCerradaFalse();

}