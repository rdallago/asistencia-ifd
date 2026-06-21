package com.instituto.asistencia.service;
import com.instituto.asistencia.entity.Jornada;
import com.instituto.asistencia.entity.Turno;
import com.instituto.asistencia.entity.Usuario;

import com.instituto.asistencia.repository.JornadaRepository;
import com.instituto.asistencia.repository.TurnoRepository;
import com.instituto.asistencia.repository.UsuarioRepository;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class JornadaService {

    private final JornadaRepository jornadaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TurnoRepository turnoRepository;
    private final AsistenciaService asistenciaService;
 
    public JornadaService(
            JornadaRepository jornadaRepository,
            UsuarioRepository usuarioRepository,
            TurnoRepository turnoRepository,
            AsistenciaService asistenciaService) {

        this.jornadaRepository = jornadaRepository;
        this.usuarioRepository = usuarioRepository;
        this.turnoRepository = turnoRepository;
        this.asistenciaService=asistenciaService;
    }

    // =====================================================
    // ABRIR JORNADA
    // =====================================================

    public Jornada abrirJornada(
            Long usuarioId,
            Long turnoId,
            String observaciones) {

        LocalDate hoy = LocalDate.now();

        if (jornadaRepository.existsByFechaAndTurno_Id(
                hoy,
                turnoId)) {

            throw new RuntimeException(
                    "La jornada ya fue creada para este turno");
        }

        Usuario usuario =
                usuarioRepository.findById(usuarioId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Usuario no encontrado"));

        Turno turno =
                turnoRepository.findById(turnoId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Turno no encontrado"));

        Jornada jornada = new Jornada();

        jornada.setFecha(hoy);
        jornada.setTurno(turno);

        jornada.setHabilitada(true);
        jornada.setCerrada(false);

        jornada.setFechaApertura(
                LocalDateTime.now());

        jornada.setUsuarioApertura(
                usuario);

        jornada.setObservaciones(
                observaciones);

        return jornadaRepository.save(
                jornada);
    }

    // =====================================================
    // CERRAR JORNADA
    // =====================================================

    public Jornada cerrarJornada(
        Long usuarioId,
        Long turnoId,
        String observaciones,
        boolean forzarCierre) {

    LocalDate hoy = LocalDate.now();

    Jornada jornada =
            jornadaRepository
                    .findByFechaAndTurno_Id(
                            hoy,
                            turnoId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "No existe jornada abierta"));

    if (jornada.getCerrada()) {

        throw new RuntimeException(
                "La jornada ya fue cerrada");
    }

    Turno turno =
            turnoRepository.findById(turnoId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Turno no encontrado"));

    LocalTime ahora = LocalTime.now();

    LocalTime horaFin = turno.getHoraFin();

    if (!forzarCierre && ahora.isBefore(horaFin)) {

        throw new RuntimeException(
                "CIERRE_ANTICIPADO");
    }

    Usuario usuario =
            usuarioRepository.findById(usuarioId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Usuario no encontrado"));

    asistenciaService.procesarCierreJornada(
            turnoId);

    jornada.setCerrada(true);

    jornada.setHabilitada(false);

    jornada.setFechaCierre(
            LocalDateTime.now());

    jornada.setUsuarioCierre(
            usuario);

    jornada.setObservaciones(
            observaciones);
    jornada.setCierreAutomatico(false);

    return jornadaRepository.save(
            jornada);
}
    
    
    // =====================================================
    // BUSCAR JORNADA
    // =====================================================

    public Jornada buscarPorFechaYTurno(
            LocalDate fecha,
            Long turnoId) {

        return jornadaRepository
                .findByFechaAndTurno_Id(
                        fecha,
                        turnoId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No existe jornada"));
    }

    // =====================================================
    // VERIFICAR SI ESTA ABIERTA
    // =====================================================

    public boolean jornadaAbierta(
            LocalDate fecha,
            Long turnoId) {

        return jornadaRepository
                .findByFechaAndTurno_Id(
                        fecha,
                        turnoId)

                .map(j ->
                        j.getHabilitada()
                        && !j.getCerrada())

                .orElse(false);
    }

    // =====================================================
    // CIERRE AUTOMATICO
    // =====================================================

    public void cerrarJornadasPendientes() {

    LocalDate hoy = LocalDate.now();

    List<Jornada> pendientes =
            jornadaRepository
                    .findByHabilitadaTrueAndCerradaFalse();

    for (Jornada jornada : pendientes) {

        if (jornada.getFecha().isBefore(hoy)) {

            jornada.setCerrada(true);

            jornada.setHabilitada(false);

            jornada.setFechaCierre(
                    LocalDateTime.now());

            jornada.setCierreAutomatico(true);
            jornadaRepository.save(jornada);
        }
    }
}
    public List<Jornada> listar() {

    return jornadaRepository.findAll();
}
    public List<Jornada> listarPorTurno(
        Long turnoId) {

    return jornadaRepository
            .findByTurno_Id(turnoId);
}
    public List<Jornada> listarAbiertas() {

    return jornadaRepository
            .findByHabilitadaTrueAndCerradaFalse();
}
    }
