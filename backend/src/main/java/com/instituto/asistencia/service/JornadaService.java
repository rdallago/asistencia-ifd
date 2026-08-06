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
    private final AuditoriaRespaldoService auditoriaRespaldoService;
 
    public JornadaService(
        JornadaRepository jornadaRepository,
        UsuarioRepository usuarioRepository,
        TurnoRepository turnoRepository,
        AsistenciaService asistenciaService,
        AuditoriaRespaldoService auditoriaRespaldoService) {

    this.jornadaRepository = jornadaRepository;
    this.usuarioRepository = usuarioRepository;
    this.turnoRepository = turnoRepository;
    this.asistenciaService = asistenciaService;
    this.auditoriaRespaldoService = auditoriaRespaldoService;
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

        Jornada guardada =
        jornadaRepository.save(jornada);

auditoriaRespaldoService.registrar(
        usuario.getUsername(),
        "JORNADAS",
        "APERTURA",
        "EXITOSO",
        null,
        "Se abrió la jornada del "
                + guardada.getFecha()
                + " - Turno "
                + turno.getNombre()
);

return guardada;
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

Jornada guardada =
        jornadaRepository.save(jornada);

auditoriaRespaldoService.registrar(
        usuario.getUsername(),
        "JORNADAS",
        "CIERRE",
        "EXITOSO",
        null,
        "Se cerró la jornada del "
                + guardada.getFecha()
                + " - Turno "
                + turno.getNombre()
);

return guardada;
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
            Jornada guardada =
        jornadaRepository.save(jornada);

auditoriaRespaldoService.registrar(
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
