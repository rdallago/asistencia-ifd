package com.instituto.asistencia.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import com.instituto.asistencia.entity.Turno;
import com.instituto.asistencia.service.TurnoService;
import org.springframework.http.ResponseEntity;
import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/turnos")
@CrossOrigin(origins = "*")
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @GetMapping
    public List<Turno> listar() {
        return turnoService.listar();
    }

    @PostMapping
    public Turno crear(@RequestBody Turno turno) {
        return turnoService.crear(turno);
    }

    @PutMapping("/{id}")
public ResponseEntity<?> actualizar(
        @PathVariable Long id,
        @RequestBody Turno turno) {

    return ResponseEntity.ok(
            turnoService.actualizar(id, turno));
}

    
 @PutMapping("/{id}/desactivar")
public ResponseEntity<?> desactivar(@PathVariable Long id) {

    try {

        turnoService.desactivar(id);

        return ResponseEntity.ok().build();

    } catch (RuntimeException e) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "message",
                        e.getMessage()
                ));
    }
}
@PutMapping("/{id}/activar")
public ResponseEntity<?> activar(@PathVariable Long id) {

    turnoService.activar(id);

    return ResponseEntity.ok().build();
}

}