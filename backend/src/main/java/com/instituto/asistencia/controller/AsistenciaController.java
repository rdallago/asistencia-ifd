package com.instituto.asistencia.controller;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.instituto.asistencia.service.AsistenciaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import com.instituto.asistencia.entity.enums.TipoReporte;
@RestController
@RequestMapping("/api/asistencia")
@CrossOrigin(origins = "*")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    public AsistenciaController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    // =========================
    // 🔵 MARCAR ENTRADA
    // =========================
    @PostMapping("/entrada")
public ResponseEntity<?> marcarEntrada(
        @RequestParam String dni,
        @RequestParam Long turnoId) {

    try {

        String respuesta =
                asistenciaService.marcarEntrada(
                        dni,
                        turnoId);

        return ResponseEntity.ok(
                respuesta);

    } catch (Exception e) {

        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }
}
   @PostMapping("/salida")
public ResponseEntity<?> marcarSalida(
        @RequestParam String dni,
        @RequestParam Long turnoId) {

    try {

        String respuesta =
                asistenciaService.marcarSalida(
                        dni,
                        turnoId);

        return ResponseEntity.ok(
                respuesta);

    } catch (Exception e) {

        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }
}
@PostMapping("/cierre")
public ResponseEntity<?> cerrarJornada(
        @RequestParam Long turnoId) {

    return ResponseEntity.ok(
            asistenciaService.procesarCierreJornada(turnoId));
}
@GetMapping("/reporte")
public ResponseEntity<?> reporte(@RequestParam String fecha) {

    LocalDate f = LocalDate.parse(fecha);

    return ResponseEntity.ok(asistenciaService.reporteDiario(f));
}

@GetMapping("/reporte/pdf")
public ResponseEntity<byte[]> generarPdf(
        @RequestParam LocalDate fecha,
        @RequestParam TipoReporte tipo) {

    byte[] pdf = asistenciaService.generarReporte(fecha, tipo);

    return ResponseEntity.ok()
            .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=reporte_asistencia.pdf"
            )
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
}
}
