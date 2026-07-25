package com.instituto.asistencia.controller;

import com.instituto.asistencia.service.ReportePdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReportePdfService reportePdfService;

    @GetMapping("/ejecutivo")
    public ResponseEntity<byte[]> reporteEjecutivo(
            @RequestParam LocalDate fecha) {

        byte[] pdf =
                reportePdfService.generarReporteEjecutivo(fecha);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Reporte_Ejecutivo.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}