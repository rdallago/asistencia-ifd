package com.instituto.asistencia.controller;

import com.instituto.asistencia.entity.AuditoriaRespaldo;
import com.instituto.asistencia.service.AuditoriaRespaldoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
@CrossOrigin(origins = "*")
public class AuditoriaController {

    private final AuditoriaRespaldoService auditoriaService;

    public AuditoriaController(
            AuditoriaRespaldoService auditoriaService) {

        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public ResponseEntity<List<AuditoriaRespaldo>> listar() {

        return ResponseEntity.ok(
                auditoriaService.listar()
        );
    }

    @GetMapping("/modulo/{modulo}")
    public ResponseEntity<List<AuditoriaRespaldo>> listarPorModulo(
            @PathVariable String modulo) {

        return ResponseEntity.ok(
                auditoriaService.listarPorModulo(modulo)
        );
    }
    
    @GetMapping("/respaldos")
public ResponseEntity<List<AuditoriaRespaldo>> listarRespaldos() {

    return ResponseEntity.ok(
            auditoriaService.listarPorModulo("RESPALDOS")
    );
}
}