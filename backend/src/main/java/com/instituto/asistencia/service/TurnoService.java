package com.instituto.asistencia.service;

import com.instituto.asistencia.entity.Turno;
import com.instituto.asistencia.repository.TurnoRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class TurnoService {

    private final TurnoRepository turnoRepository;

    public TurnoService(
            TurnoRepository turnoRepository) {

        this.turnoRepository = turnoRepository;
    }

    public List<Turno> listarTodos() {
        return turnoRepository.findAll();
    }

    public Turno buscarPorId(Long id) {

        return turnoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Turno no encontrado"));
    }
}