package com.instituto.asistencia.controller;

import com.instituto.asistencia.dto.UsuarioRequest;
import com.instituto.asistencia.service.UsuarioService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(
            UsuarioService usuarioService) {

        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<?> listar() {

        return ResponseEntity.ok(
                usuarioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                usuarioService.buscarPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(
            @RequestParam String texto) {

        return ResponseEntity.ok(
                usuarioService.buscar(texto));
    }

    @PostMapping
    public ResponseEntity<?> crear(
            @RequestBody UsuarioRequest request) {

        try {

            return ResponseEntity.ok(
                    usuarioService.crear(request));

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody UsuarioRequest request) {

        try {

            return ResponseEntity.ok(
                    usuarioService.actualizar(
                            id,
                            request));

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> desactivar(
            @PathVariable Long id) {

        try {

            usuarioService.desactivar(id);

            return ResponseEntity.ok(
                    "Usuario desactivado");

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
    @PutMapping("/{id}/activar")
public ResponseEntity<?> activar(
        @PathVariable Long id) {

    try {

        usuarioService.activar(id);

        return ResponseEntity.ok(
                "Usuario activado");

    } catch (Exception e) {

        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }
}
}