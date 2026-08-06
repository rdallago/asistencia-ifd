package com.instituto.asistencia.service;

import com.instituto.asistencia.entity.Docente;
import com.instituto.asistencia.entity.DocenteTurno;
import com.instituto.asistencia.entity.Turno;
import com.instituto.asistencia.repository.DocenteRepository;
import com.instituto.asistencia.repository.DocenteTurnoRepository;
import com.instituto.asistencia.repository.TurnoRepository;
import java.time.LocalDate;
import com.instituto.asistencia.dto.AsignacionRequest;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class AsignacionService {

    private final DocenteTurnoRepository docenteTurnoRepository;
    private final DocenteRepository docenteRepository;
    private final TurnoRepository turnoRepository;
    private final AuditoriaRespaldoService auditoriaRespaldoService;

    public AsignacionService(
        DocenteTurnoRepository docenteTurnoRepository,
        DocenteRepository docenteRepository,
        TurnoRepository turnoRepository,
        AuditoriaRespaldoService auditoriaRespaldoService) {

    this.docenteTurnoRepository = docenteTurnoRepository;
    this.docenteRepository = docenteRepository;
    this.turnoRepository = turnoRepository;
    this.auditoriaRespaldoService = auditoriaRespaldoService;
}

    public List<DocenteTurno> listar() {

        return docenteTurnoRepository.findAll();
    }

    public List<DocenteTurno> listarActivas() {

        return docenteTurnoRepository.findByActivoTrue();
    }

    public DocenteTurno crear(AsignacionRequest request) {

    boolean existe = docenteTurnoRepository
            .existsByDocente_IdAndTurno_IdAndActivoTrue(
                    request.getDocenteId(),
                    request.getTurnoId()
            );

    if (existe) {
        throw new RuntimeException(
                "El docente ya se encuentra asignado al turno seleccionado."
        );
    }

    Docente docente = docenteRepository
            .findById(request.getDocenteId())
            .orElseThrow(() ->
                    new RuntimeException("Docente no encontrado"));

    Turno turno = turnoRepository
            .findById(request.getTurnoId())
            .orElseThrow(() ->
                    new RuntimeException("Turno no encontrado"));

    DocenteTurno asignacion = new DocenteTurno();

    asignacion.setDocente(docente);
    asignacion.setTurno(turno);
    asignacion.setFechaDesde(request.getFechaDesde());
    asignacion.setActivo(true);

    DocenteTurno guardada =
        docenteTurnoRepository.save(asignacion);

Authentication authentication =
        SecurityContextHolder
                .getContext()
                .getAuthentication();

String usuarioActual =
        authentication.getName();

auditoriaRespaldoService.registrar(
        usuarioActual,
        "ASIGNACIONES",
        "ALTA",
        "EXITOSO",
        null,
        "Se asignó "
                + docente.getApellido()
                + ", "
                + docente.getNombre()
                + " al turno "
                + turno.getNombre()
);

return guardada;
}

    public void desasignar(Long id) {

        DocenteTurno asignacion =
                docenteTurnoRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Asignación no encontrada"));

        asignacion.setActivo(false);
        asignacion.setFechaHasta(LocalDate.now());

        DocenteTurno guardada =
        docenteTurnoRepository.save(asignacion);

Authentication authentication =
        SecurityContextHolder
                .getContext()
                .getAuthentication();

String usuarioActual =
        authentication.getName();

auditoriaRespaldoService.registrar(
        usuarioActual,
        "ASIGNACIONES",
        "BAJA",
        "EXITOSO",
        null,
        "Se quitó la asignación de "
                + guardada.getDocente().getApellido()
                + ", "
                + guardada.getDocente().getNombre()
                + " del turno "
                + guardada.getTurno().getNombre()
);
    }
    public void reactivar(Long id) {

    DocenteTurno asignacion = docenteTurnoRepository
            .findById(id)
            .orElseThrow(() ->
                    new RuntimeException(
                            "Asignación no encontrada"));

    boolean existeActiva = docenteTurnoRepository
            .existsByDocente_IdAndTurno_IdAndActivoTrue(
                    asignacion.getDocente().getId(),
                    asignacion.getTurno().getId()
            );

    if (existeActiva) {
        throw new RuntimeException(
                "El docente ya posee una asignación activa para ese turno");
    }

    asignacion.setActivo(true);
    asignacion.setFechaHasta(null);

    DocenteTurno guardada =
        docenteTurnoRepository.save(asignacion);

Authentication authentication =
        SecurityContextHolder
                .getContext()
                .getAuthentication();

String usuarioActual =
        authentication.getName();

auditoriaRespaldoService.registrar(
        usuarioActual,
        "ASIGNACIONES",
        "REACTIVACION",
        "EXITOSO",
        null,
        "Se reactivó la asignación de "
                + guardada.getDocente().getApellido()
                + ", "
                + guardada.getDocente().getNombre()
                + " al turno "
                + guardada.getTurno().getNombre()
);
}
}