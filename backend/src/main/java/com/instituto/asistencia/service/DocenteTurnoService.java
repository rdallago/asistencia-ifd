package com.instituto.asistencia.service;

import com.instituto.asistencia.dto.AsignacionRequest;
import com.instituto.asistencia.entity.Docente;
import com.instituto.asistencia.entity.DocenteTurno;
import com.instituto.asistencia.entity.Turno;
import com.instituto.asistencia.repository.DocenteRepository;
import com.instituto.asistencia.repository.DocenteTurnoRepository;
import com.instituto.asistencia.repository.TurnoRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DocenteTurnoService {

    private final DocenteTurnoRepository docenteTurnoRepository;
    private final DocenteRepository docenteRepository;
    private final TurnoRepository turnoRepository;
    private final AuditoriaRespaldoService auditoriaRespaldoService;
    public DocenteTurnoService(
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

    public DocenteTurno asignar(AsignacionRequest request) {

        if (docenteTurnoRepository.existsByDocente_IdAndTurno_Id(
                request.getDocenteId(),
                request.getTurnoId())) {

            throw new RuntimeException(
                    "El docente ya está asignado a ese turno");
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
        asignacion.setActivo(true);

        asignacion.setFechaDesde(
                request.getFechaDesde() != null
                        ? request.getFechaDesde()
                        : LocalDate.now());

        DocenteTurno guardada =
        docenteTurnoRepository.save(asignacion);

Authentication authentication =
        SecurityContextHolder
                .getContext()
                .getAuthentication();

String usuarioActual =
        authentication.getName();
System.out.println(">>> AUDITORIA ASIGNACION");
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
System.out.println(">>> AUDITORIA REGISTRADA");
return guardada;
    }

    public void finalizar(Long id) {

        DocenteTurno asignacion =
                docenteTurnoRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Asignación no encontrada"));

        asignacion.setActivo(false);
        asignacion.setFechaHasta(LocalDate.now());

        docenteTurnoRepository.save(asignacion);
    }
}