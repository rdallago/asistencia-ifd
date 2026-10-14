package com.instituto.asistencia.service;

import com.instituto.asistencia.entity.Jornada;
import com.instituto.asistencia.repository.JornadaRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CierreJornadaIndividualService {

    private final JornadaRepository jornadaRepository;
    private final AsistenciaService asistenciaService;
    private final AuditoriaRespaldoService auditoriaRespaldoService;

    public CierreJornadaIndividualService(
            JornadaRepository jornadaRepository,
            AsistenciaService asistenciaService,
            AuditoriaRespaldoService auditoriaRespaldoService) {

        this.jornadaRepository = jornadaRepository;
        this.asistenciaService = asistenciaService;
        this.auditoriaRespaldoService = auditoriaRespaldoService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
public void cerrar(Long jornadaId) {

    Jornada jornada = jornadaRepository.findById(jornadaId)
            .orElseThrow(() ->
                    new RuntimeException(
                            "No existe la jornada con ID: " + jornadaId
                    )
            );

    if (Boolean.TRUE.equals(jornada.getCerrada())
            || !Boolean.TRUE.equals(jornada.getHabilitada())) {
        throw new IllegalStateException(
                "La jornada ya está cerrada o no está habilitada"
        );
    }

    if (!jornada.getFecha().isBefore(java.time.LocalDate.now())) {
        throw new IllegalStateException(
                "Solo pueden cerrarse automáticamente jornadas anteriores"
        );
    }

        // Procesar las asistencias de la fecha original.
        asistenciaService.procesarCierreJornada(jornada);

        // Cerrar la jornada.
        jornada.setCerrada(true);
        jornada.setHabilitada(false);
        jornada.setFechaCierre(LocalDateTime.now());
        jornada.setCierreAutomatico(true);

        Jornada guardada = jornadaRepository.save(jornada);

        // Registrar la auditoría en la misma transacción.
        auditoriaRespaldoService.registrarEnTransaccion(
                "SISTEMA",
                "JORNADAS",
                "CIERRE_AUTOMATICO",
                "EXITOSO",
                null,
                "Se cerró automáticamente la jornada del "
                        + guardada.getFecha()
                        + " - Turno "
                        + guardada.getTurno().getNombre()
        );
    }
}