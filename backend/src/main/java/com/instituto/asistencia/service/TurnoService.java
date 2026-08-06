package com.instituto.asistencia.service;

import com.instituto.asistencia.entity.Institucion;
import com.instituto.asistencia.entity.Jornada;
import com.instituto.asistencia.entity.Turno;
import com.instituto.asistencia.repository.InstitucionRepository;
import com.instituto.asistencia.repository.JornadaRepository;
import com.instituto.asistencia.repository.TurnoRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final JornadaRepository jornadaRepository;
    private final InstitucionRepository institucionRepository;
    private final AuditoriaRespaldoService auditoriaRespaldoService;

public TurnoService(
        TurnoRepository turnoRepository,
        JornadaRepository jornadaRepository,
        InstitucionRepository institucionRepository,
        AuditoriaRespaldoService auditoriaRespaldoService) {

    this.turnoRepository = turnoRepository;
    this.jornadaRepository = jornadaRepository;
    this.institucionRepository = institucionRepository;
    this.auditoriaRespaldoService = auditoriaRespaldoService;
}

    // LISTAR TODOS
    public List<Turno> listar() {
        return turnoRepository.findAll();
    }

    // CREAR
    public Turno crear(Turno turno) {

        validarTurno(turno);

        Institucion institucion =
                institucionRepository.findById(1L)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "No existe la institución configurada"));

        turno.setInstitucion(institucion);

        Turno guardado = turnoRepository.save(turno);

Authentication authentication =
        SecurityContextHolder
                .getContext()
                .getAuthentication();

String usuario = authentication.getName();

auditoriaRespaldoService.registrar(
        usuario,
        "TURNOS",
        "ALTA",
        "EXITOSO",
        null,
        "Se creó el turno: "
                + guardado.getNombre()
);

return guardado;
    }

    // ACTUALIZAR
    public Turno actualizar(Long id, Turno turnoActualizado) {

        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Turno no encontrado"));

        if (Boolean.TRUE.equals(turno.getActivo())
                && Boolean.FALSE.equals(turnoActualizado.getActivo())) {

            validarDesactivacion(id);
        }

        turno.setNombre(turnoActualizado.getNombre());
        turno.setHoraInicio(turnoActualizado.getHoraInicio());
        turno.setHoraFin(turnoActualizado.getHoraFin());
        turno.setActivo(turnoActualizado.getActivo());

        validarTurno(turno);

       Turno guardado = turnoRepository.save(turno);

Authentication authentication =
        SecurityContextHolder
                .getContext()
                .getAuthentication();

String usuario = authentication.getName();

auditoriaRespaldoService.registrar(
        usuario,
        "TURNOS",
        "MODIFICACION",
        "EXITOSO",
        null,
        "Se modificó el turno: "
                + guardado.getNombre()
);

return guardado;
    }

    // VALIDACIONES
    private void validarTurno(Turno turno) {

        if (turno.getHoraInicio().isAfter(turno.getHoraFin())) {

            throw new RuntimeException(
                    "La hora de inicio no puede ser mayor a la hora de fin");
        }

        boolean duplicado = turnoRepository.findAll()
                .stream()
                .anyMatch(t ->
                        t.getNombre().equalsIgnoreCase(turno.getNombre())
                                && Boolean.TRUE.equals(t.getActivo())
                                && (turno.getId() == null
                                || !t.getId().equals(turno.getId()))
                );

        if (duplicado) {

            throw new RuntimeException(
                    "Ya existe un turno activo con ese nombre");
        }
    }

    private void validarDesactivacion(Long turnoId) {

        boolean tieneJornadas =
                jornadaRepository.existsByTurno_Id(turnoId);

        if (tieneJornadas) {

            throw new RuntimeException(
                    "No se puede desactivar un turno que posee jornadas asociadas");
        }
    }

    // DESACTIVAR
    public void desactivar(Long id) {

        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Turno no encontrado"));

        validarDesactivacion(id);

        turno.setActivo(false);

        Authentication authentication =
        SecurityContextHolder
                .getContext()
                .getAuthentication();

String usuario = authentication.getName();

auditoriaRespaldoService.registrar(
        usuario,
        "TURNOS",
        "BAJA",
        "EXITOSO",
        null,
        "Se desactivó el turno: "
                + turno.getNombre()
);
    }

    // ACTIVAR
    public void activar(Long id) {

        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Turno no encontrado"));

        turno.setActivo(true);

        Authentication authentication =
        SecurityContextHolder
                .getContext()
                .getAuthentication();

String usuario = authentication.getName();

auditoriaRespaldoService.registrar(
        usuario,
        "TURNOS",
        "REACTIVACION",
        "EXITOSO",
        null,
        "Se reactivó el turno: "
                + turno.getNombre()
);
    }

}