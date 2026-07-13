package com.instituto.asistencia.repository;

import com.instituto.asistencia.entity.Asistencia;
import com.instituto.asistencia.entity.enums.EstadoAsistencia;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    // =========================================
    // BUSQUEDA BASE OPERATIVA
    // =========================================

    Optional<Asistencia> findByDocenteIdAndTurnoIdAndJornadaId(Long docenteId, Long turnoId, Long jornadaId);

    List<Asistencia> findByJornadaId(Long jornadaId);

    List<Asistencia> findByTurnoId(Long turnoId);

    List<Asistencia> findByDocenteId(Long docenteId);

    // =========================================
    // ESTADOS
    // =========================================

    List<Asistencia> findByJornadaIdAndEstado(Long jornadaId, EstadoAsistencia estado);

    List<Asistencia> findByTurnoIdAndEstado(Long turnoId, EstadoAsistencia estado);

    // =========================================
    // CONTROL DE EXISTENCIA
    // =========================================

    boolean existsByDocenteIdAndTurnoIdAndJornadaId(Long docenteId, Long turnoId, Long jornadaId);

    // =========================================
    // CASOS ESPECIALES DE CONSULTA
    // =========================================

    List<Asistencia> findByJornadaIdAndHoraIngresoIsNull(Long jornadaId);

    List<Asistencia> findByJornadaIdAndHoraIngresoIsNotNullAndHoraEgresoIsNull(Long jornadaId);
    
    @Query("""
SELECT a FROM Asistencia a
WHERE a.jornada.id = :jornadaId
AND a.turno.id = :turnoId
ORDER BY a.docente.apellido
""")
List<Asistencia> findPlanillaJornadaTurno(Long jornadaId, Long turnoId);
List<Asistencia> findByFecha(LocalDate fecha);

Optional<Asistencia>
findByDocenteIdAndTurnoIdAndFecha(
        Long docenteId,
        Long turnoId,
        LocalDate fecha);

List<Asistencia> findByFechaAndTurnoId(
        LocalDate fecha,
        Long turnoId);

List<Asistencia> findByFechaAndDocenteId(
        LocalDate fecha,
        Long docenteId);

List<Asistencia> findByFechaBetweenOrderByFecha(
        LocalDate desde,
        LocalDate hasta);

}
