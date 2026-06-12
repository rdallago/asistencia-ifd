package com.instituto.asistencia.service;
import com.instituto.asistencia.entity.DocenteTurno;
import com.instituto.asistencia.entity.Asistencia;
import com.instituto.asistencia.entity.Docente;
import com.instituto.asistencia.entity.Jornada;
import com.instituto.asistencia.entity.Turno;
import java.util.List;
import com.instituto.asistencia.entity.enums.EstadoAsistencia;
import com.instituto.asistencia.entity.enums.TipoReporte;

import com.instituto.asistencia.repository.AsistenciaRepository;
import com.instituto.asistencia.repository.DocenteRepository;
import com.instituto.asistencia.repository.JornadaRepository;
import com.instituto.asistencia.repository.TurnoRepository;
import com.instituto.asistencia.repository.DocenteTurnoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.List;

@Service
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final DocenteRepository docenteRepository;
    private final TurnoRepository turnoRepository;
    private final JornadaRepository jornadaRepository;
    private final DocenteTurnoRepository docenteTurnoRepository;
    public AsistenciaService(
            AsistenciaRepository asistenciaRepository,
            DocenteRepository docenteRepository,
            TurnoRepository turnoRepository,
            JornadaRepository jornadaRepository,DocenteTurnoRepository docenteTurnoRepository) {

        this.asistenciaRepository = asistenciaRepository;
        this.docenteRepository = docenteRepository;
        this.turnoRepository = turnoRepository;
        this.jornadaRepository = jornadaRepository;
        this.docenteTurnoRepository = docenteTurnoRepository;
    }

    // =====================================================
    // MARCAR INGRESO
    // =====================================================

    public String marcarEntrada(
            String dni,
            Long turnoId) {

        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();

        Jornada jornada =obtenerJornadaActiva(turnoId);

        Docente docente =
                docenteRepository.findByDni(dni)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Docente no encontrado"));

        Turno turno =
                turnoRepository.findById(turnoId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Turno no encontrado"));

        if (ahora.isBefore(turno.getHoraInicio())) {

            throw new RuntimeException(
                    "Aún no comenzó el turno");
        }

        Asistencia asistencia =
                asistenciaRepository
                        .findByDocenteIdAndTurnoIdAndFecha(
                                docente.getId(),
                                turnoId,
                                hoy)
                        .orElse(new Asistencia());

        asistencia.setDocente(docente);
        asistencia.setTurno(turno);
        asistencia.setJornada(jornada);
        asistencia.setFecha(hoy);

        if (asistencia.getHoraIngreso() != null) {

            throw new RuntimeException(
                    "La entrada ya fue registrada");
        }

        asistencia.setHoraIngreso(ahora);

        asistencia.setEstado(
                EstadoAsistencia.ABIERTO);

        asistencia.setFechaCreacion(
                LocalDateTime.now());

        asistenciaRepository.save(asistencia);

        return "Entrada registrada correctamente";
    }

    // =====================================================
    // MARCAR EGRESO
    // =====================================================

    public String marcarSalida(
            String dni,
            Long turnoId) {

        LocalDate hoy = LocalDate.now();

      Jornada jornada =obtenerJornadaActiva(turnoId);


        Docente docente =
                docenteRepository.findByDni(dni)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Docente no encontrado"));

        Asistencia asistencia =
                asistenciaRepository
                        .findByDocenteIdAndTurnoIdAndFecha(
                                docente.getId(),
                                turnoId,
                                hoy)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "No existe ingreso registrado"));

        if (asistencia.getHoraEgreso() != null) {

            throw new RuntimeException(
                    "El egreso ya fue registrado");
        }

        asistencia.setHoraEgreso(
                LocalTime.now());

        asistencia.setEstado(
                EstadoAsistencia.PRESENTE);

        asistencia.setFechaModificacion(
                LocalDateTime.now());

        asistenciaRepository.save(asistencia);

        return "Salida registrada correctamente";
    }

    // =====================================================
    // CIERRE DE JORNADA
    // =====================================================

public String procesarCierreJornada(Long turnoId) {

    LocalDate hoy = LocalDate.now();

    Jornada jornada =
            jornadaRepository
                    .findByFechaAndTurnoId(
                            hoy,
                            turnoId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "No existe jornada habilitada"));

    Turno turno = jornada.getTurno();

    List<DocenteTurno> asignados =
            docenteTurnoRepository
                    .findByTurno_Id(
                            turno.getId());

    for (DocenteTurno dt : asignados) {

        Docente docente =
                dt.getDocente();

        Asistencia asistencia =
                asistenciaRepository
                        .findByDocenteIdAndTurnoIdAndFecha(
                                docente.getId(),
                                turno.getId(),
                                hoy)
                        .orElse(null);

        // =====================================
        // SIN FIRMA TOTAL
        // =====================================

        if (asistencia == null) {

            asistencia = new Asistencia();

            asistencia.setDocente(docente);
            asistencia.setTurno(turno);
            asistencia.setJornada(jornada);

            asistencia.setFecha(hoy);

            asistencia.setEstado(
                    EstadoAsistencia.SIN_FIRMA_TOTAL);

            asistencia.setFechaCreacion(
                    LocalDateTime.now());

            asistenciaRepository.save(
                    asistencia);

            continue;
        }

        // =====================================
        // INGRESO SIN EGRESO
        // =====================================

        if (asistencia.getHoraIngreso() != null
                && asistencia.getHoraEgreso() == null) {

            asistencia.setEstado(
                    EstadoAsistencia.SIN_EGRESO);

            asistencia.setFechaModificacion(
                    LocalDateTime.now());

            asistenciaRepository.save(
                    asistencia);

            continue;
        }

        // =====================================
        // PRESENTE
        // =====================================

        if (asistencia.getHoraIngreso() != null
                && asistencia.getHoraEgreso() != null) {

            asistencia.setEstado(
                    EstadoAsistencia.PRESENTE);

            asistencia.setFechaModificacion(
                    LocalDateTime.now());

            asistenciaRepository.save(
                    asistencia);
        }
    }

    
    return "Asistencia procesada correctamente";
}
// =====================================================
    // REPORTE DIARIO
    // =====================================================

    public List<Asistencia> reporteDiario(
            LocalDate fecha) {

        return asistenciaRepository
                .findByFecha(fecha);
    }

    // =====================================================
    // GENERACION PDF
    // =====================================================

    public byte[] generarReporte(
            LocalDate fecha,
            TipoReporte tipo) {

        return new byte[0];
    }
    
    
    private Jornada obtenerJornadaActiva(
        Long turnoId) {

    LocalDate hoy = LocalDate.now();

    Jornada jornada =
            jornadaRepository
                    .findByFechaAndTurnoId(
                            hoy,
                            turnoId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "No existe jornada habilitada"));

    if (!jornada.getHabilitada()) {

        throw new RuntimeException(
                "La jornada no está habilitada");
    }

    if (jornada.getCerrada()) {

        throw new RuntimeException(
                "La jornada ya fue cerrada");
    }

    return jornada;
}
}