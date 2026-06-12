package com.instituto.asistencia.controller;

import com.instituto.asistencia.entity.Turno;
import com.instituto.asistencia.service.TurnoService;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/turnos")
@CrossOrigin(origins = "*")
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(
            TurnoService turnoService) {

        this.turnoService = turnoService;
    }

    @GetMapping
    public List<Turno> listar() {
        return turnoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Turno buscar(
            @PathVariable Long id) {

        return turnoService.buscarPorId(id);
    }
}