package com.instituto.asistencia.controller;

import com.instituto.asistencia.entity.Jornada;
import com.instituto.asistencia.service.JornadaService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jornadas")
@CrossOrigin(origins = "*")
public class JornadaController {

    private final JornadaService jornadaService;

    public JornadaController(
            JornadaService jornadaService) {

        this.jornadaService = jornadaService;
    }

    // ==========================================
    // ABRIR JORNADA
    // ==========================================

    @PostMapping("/abrir")
    public ResponseEntity<?> abrirJornada(

            @RequestParam Long usuarioId,

            @RequestParam Long turnoId,

            @RequestParam(
                    required = false,
                    defaultValue = "")
            String observaciones) {

        try {

            Jornada jornada =
                    jornadaService.abrirJornada(
                            usuarioId,
                            turnoId,
                            observaciones);

            return ResponseEntity.ok(
                    jornada);

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    // ==========================================
    // CERRAR JORNADA
    // ==========================================

@PostMapping("/cerrar")
public ResponseEntity<?> cerrarJornada(

        @RequestParam Long usuarioId,

        @RequestParam Long turnoId,

        @RequestParam(
                required = false,
                defaultValue = "")
        String observaciones,

        @RequestParam(
                required = false,
                defaultValue = "false")
        boolean forzarCierre) {

    try {

        Jornada jornada =
                jornadaService.cerrarJornada(
                        usuarioId,
                        turnoId,
                        observaciones,
                        forzarCierre);

        return ResponseEntity.ok(
                jornada);

    } catch (Exception e) {

        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }
}
@GetMapping("/abierta")
public ResponseEntity<?> jornadaAbierta(
        @RequestParam String fecha,
        @RequestParam Long turnoId) {

    return ResponseEntity.ok(
            jornadaService.jornadaAbierta(
                    java.time.LocalDate.parse(fecha),
                    turnoId));
}
@GetMapping("/buscar")
public ResponseEntity<?> buscar(

        @RequestParam String fecha,

        @RequestParam Long turnoId) {

    return ResponseEntity.ok(
            jornadaService.buscarPorFechaYTurno(
                    java.time.LocalDate.parse(fecha),
                    turnoId));
}
@GetMapping
public ResponseEntity<?> listar() {

    return ResponseEntity.ok(
            jornadaService.listar());
}
@GetMapping("/turno/{turnoId}")
public ResponseEntity<?> listarPorTurno(
        @PathVariable Long turnoId) {

    return ResponseEntity.ok(
            jornadaService
                    .listarPorTurno(turnoId));
}
@GetMapping("/abiertas")
public ResponseEntity<?> listarAbiertas() {

    return ResponseEntity.ok(
            jornadaService.listarAbiertas());
}
}