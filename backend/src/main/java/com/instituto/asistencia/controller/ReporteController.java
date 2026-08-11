package com.instituto.asistencia.controller;

import com.instituto.asistencia.service.ReportePdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import com.instituto.asistencia.entity.Turno;
import com.instituto.asistencia.service.TurnoService;
import com.instituto.asistencia.entity.Docente;
import com.instituto.asistencia.service.DocenteService;
import com.instituto.asistencia.service.AsistenciaService;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReportePdfService reportePdfService;
    private final DocenteService docenteService;
    private final AsistenciaService asistenciaService;
    private final TurnoService turnoService; 
    
    
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

@GetMapping("/docentes")
public ResponseEntity<byte[]> reporteDocentes() {

    List<Docente> docentes =
            docenteService.listarParaReporte();

    byte[] pdf =
            reportePdfService.generarReporteDocentes(docentes);

    return ResponseEntity.ok()
            .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=Reporte_Docentes.pdf"
            )
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
}
    
    
    
@GetMapping("/docentes/pdf")
public ResponseEntity<byte[]> reporteDocentesPdf() {

    List<Docente> docentes =
            docenteService.listarParaReporte();

    byte[] pdf =
            reportePdfService.generarReporteDocentes(docentes);

    return ResponseEntity.ok()
            .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=Reporte_Docentes.pdf"
            )
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
}


@GetMapping("/turnos/pdf")
public ResponseEntity<byte[]> reporteTurnosPdf() {

    List<Turno> turnos = turnoService.listarParaReporte();

    byte[] pdf =
            reportePdfService.generarReporteTurnos(turnos);

    return ResponseEntity.ok()
            .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=reporte_turnos.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
}
}