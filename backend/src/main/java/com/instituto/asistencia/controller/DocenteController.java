package com.instituto.asistencia.controller;

import com.instituto.asistencia.entity.Docente;
import com.instituto.asistencia.service.DocenteService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/docentes")
@CrossOrigin(origins = "*")
public class DocenteController {

    private final DocenteService docenteService;

    public DocenteController(
            DocenteService docenteService) {

        this.docenteService = docenteService;
    }

    // ============================
    // LISTAR
    // ============================

    @GetMapping
    public List<Docente> listar() {
        return docenteService.listarTodos();
    }

    // ============================
    // BUSCAR POR ID
    // ============================

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(
            @PathVariable Long id) {

        try {

            return ResponseEntity.ok(
                    docenteService.buscarPorId(id));

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    // ============================
    // CREAR
    // ============================

    @PostMapping
    public ResponseEntity<?> guardar(
            @RequestBody Docente docente) {

        try {

            return ResponseEntity.ok(
                    docenteService.guardar(docente));

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    // ============================
    // MODIFICAR
    // ============================

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody Docente docente) {

        try {

            return ResponseEntity.ok(
                    docenteService.actualizar(
                            id,
                            docente));

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    // ============================
    // ELIMINAR
    // ============================

    @DeleteMapping("/{id}")
public ResponseEntity<?> eliminar(
        @PathVariable Long id) {

    try {

        docenteService.eliminar(id);

        Map<String, String> respuesta =
                new HashMap<>();

        respuesta.put(
                "mensaje",
                "Docente eliminado correctamente");

        return ResponseEntity.ok(respuesta);

    } catch (Exception e) {

        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }
}

}