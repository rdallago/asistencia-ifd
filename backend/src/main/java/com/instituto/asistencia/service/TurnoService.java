package com.instituto.asistencia.service;

import com.instituto.asistencia.entity.Institucion;
import com.instituto.asistencia.entity.Jornada;
import com.instituto.asistencia.entity.Turno;
import com.instituto.asistencia.repository.InstitucionRepository;
import com.instituto.asistencia.repository.JornadaRepository;
import com.instituto.asistencia.repository.TurnoRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final JornadaRepository jornadaRepository;
    private final InstitucionRepository institucionRepository;

    public TurnoService(
            TurnoRepository turnoRepository,
            JornadaRepository jornadaRepository,
            InstitucionRepository institucionRepository) {

        this.turnoRepository = turnoRepository;
        this.jornadaRepository = jornadaRepository;
        this.institucionRepository = institucionRepository;
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

        return turnoRepository.save(turno);
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

        return turnoRepository.save(turno);
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

        turnoRepository.save(turno);
    }

    // ACTIVAR
    public void activar(Long id) {

        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Turno no encontrado"));

        turno.setActivo(true);

        turnoRepository.save(turno);
    }

}