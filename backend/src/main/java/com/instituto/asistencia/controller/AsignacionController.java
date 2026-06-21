package com.instituto.asistencia.controller;

import com.instituto.asistencia.dto.AsignacionRequest;
import com.instituto.asistencia.entity.DocenteTurno;
import com.instituto.asistencia.service.AsignacionService;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/asignaciones")
@CrossOrigin(origins = "*")
public class AsignacionController {

    private final AsignacionService asignacionService;

    public AsignacionController(
            AsignacionService asignacionService) {

        this.asignacionService = asignacionService;
    }

    @GetMapping
    public ResponseEntity<List<DocenteTurno>> listar() {

        return ResponseEntity.ok(
                asignacionService.listar());
    }

    @GetMapping("/activas")
    public ResponseEntity<List<DocenteTurno>> listarActivas() {

        return ResponseEntity.ok(
                asignacionService.listarActivas());
    }

   @PostMapping
public ResponseEntity<?> crear(
        @RequestBody AsignacionRequest request) {

    try {

        return ResponseEntity.ok(
                asignacionService.crear(request)
        );

    } catch (RuntimeException e) {

        return ResponseEntity.badRequest().body(
                Map.of("message", e.getMessage())
        );
    }
}
    @PutMapping("/{id}/desasignar")
    public ResponseEntity<?> desasignar(
            @PathVariable Long id) {

        asignacionService.desasignar(id);

        return ResponseEntity.ok().build();
    }
    @PutMapping("/{id}/reactivar")
public ResponseEntity<?> reactivar(
        @PathVariable Long id) {

    asignacionService.reactivar(id);

    return ResponseEntity.ok().build();
}
}