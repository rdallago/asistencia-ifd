package com.instituto.asistencia.controller;

import com.instituto.asistencia.entity.Institucion;
import com.instituto.asistencia.service.InstitucionService;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/instituciones")
@CrossOrigin(origins = "*")
public class InstitucionController {

    private final InstitucionService institucionService;

    public InstitucionController(
            InstitucionService institucionService) {

        this.institucionService = institucionService;
    }

    @GetMapping
    public List<Institucion> listar() {

        return institucionService.listarTodas();
    }

    @GetMapping("/{id}")
    public Institucion buscar(
            @PathVariable Long id) {

        return institucionService.buscarPorId(id);
    }

    @PostMapping
    public Institucion guardar(
            @RequestBody Institucion institucion) {

        return institucionService.guardar(
                institucion);
    }
}