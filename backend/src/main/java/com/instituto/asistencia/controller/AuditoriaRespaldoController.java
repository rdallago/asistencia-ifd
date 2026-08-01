package com.instituto.asistencia.controller;

import com.instituto.asistencia.entity.AuditoriaRespaldo;
import com.instituto.asistencia.service.AuditoriaRespaldoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/respaldos/auditoria")
public class AuditoriaRespaldoController {

    private final AuditoriaRespaldoService auditoriaService;

    public AuditoriaRespaldoController(
            AuditoriaRespaldoService auditoriaService) {

        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditoriaRespaldo>> listar() {

        return ResponseEntity.ok(
                auditoriaService.listar()
        );
    }
}