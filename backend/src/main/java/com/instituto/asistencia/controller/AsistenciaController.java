package com.instituto.asistencia.controller;
import com.instituto.asistencia.entity.Usuario;
import org.springframework.security.core.Authentication;
import com.instituto.asistencia.exportador.ExcelExporter;
import com.instituto.asistencia.dto.ReporteAsistenciaDTO;
import java.util.List;
import com.instituto.asistencia.dto.ComparacionHistoricaDTO;
import com.instituto.asistencia.dto.EstadoGeneralJornadaDTO;
import com.instituto.asistencia.dto.ResumenEjecutivoDTO;
import com.instituto.asistencia.dto.EvolucionAsistenciaDTO;
import com.instituto.asistencia.dto.EstadisticasTurnoDTO;
import com.instituto.asistencia.dto.EstadisticasDTO;
import org.springframework.security.core.context.SecurityContextHolder;
import com.instituto.asistencia.service.AsistenciaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.time.LocalDate;
import com.instituto.asistencia.repository.UsuarioRepository;
import com.instituto.asistencia.dto.AsistenciaRequest;
import com.instituto.asistencia.dto.IndicadoresInstitucionalesDTO;

@RestController
@RequestMapping("/api/asistencia")
@CrossOrigin(origins = "*")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;
    private final UsuarioRepository usuarioRepository;

    public AsistenciaController(AsistenciaService asistenciaService,UsuarioRepository usuarioRepository) {
        this.asistenciaService = asistenciaService;
        this.usuarioRepository=usuarioRepository;
    }
    
  

    // =========================
    // 🔵 MARCAR ENTRADA
    // =========================
 @PostMapping("/entrada")
public ResponseEntity<?> marcarEntrada(
        @RequestBody AsistenciaRequest request) {

    try {

        return ResponseEntity.ok(
                asistenciaService.marcarEntrada(
                        request.getDni(),
                        request.getJornadaId()));

    } catch (Exception e) {

        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }
}


  @PostMapping("/salida")
public ResponseEntity<?> marcarSalida(
        @RequestBody AsistenciaRequest request) {

    try {

        return ResponseEntity.ok(
                asistenciaService.marcarSalida(
                        request.getDni(),
                        request.getJornadaId()));

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

@GetMapping("/pdf")
public ResponseEntity<byte[]> generarPdf(

        @RequestParam String fecha,

        @RequestParam(required = false)
        Long turnoId,

        @RequestParam(required = false)
        Long docenteId,

        @RequestParam(required = false)
        String estado) {
    
    Authentication auth =
        SecurityContextHolder
                .getContext()
                .getAuthentication();

String username = auth.getName();

Usuario usuario =
        usuarioRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));

String nombreCompleto =
        usuario.getApellido()
        + ", "
        + usuario.getNombre();

    byte[] pdf = asistenciaService.generarPdf(

            LocalDate.parse(fecha),

            turnoId,

            docenteId,

            estado,nombreCompleto);

    return ResponseEntity.ok()

            .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=reporte_asistencia.pdf")

            .contentType(MediaType.APPLICATION_PDF)

            .body(pdf);
}
@GetMapping("/reporte")
public ResponseEntity<?> reporte(

        @RequestParam String fecha,

        @RequestParam(required = false)
        Long turnoId,

        @RequestParam(required = false)
        Long docenteId,

        @RequestParam(required = false)
        String estado) {

    return ResponseEntity.ok(

            asistenciaService.reporte(

                    LocalDate.parse(fecha),

                    turnoId,

                    docenteId,

                    estado
            ));
}
@GetMapping("/excel")
public ResponseEntity<byte[]> excel(

        @RequestParam String fecha,

        @RequestParam(required = false)
        Long turnoId,

        @RequestParam(required = false)
        Long docenteId,

        @RequestParam(required = false)
        String estado

) throws Exception {

    List<ReporteAsistenciaDTO> datos =
            asistenciaService.reporte(
                    LocalDate.parse(fecha),
                    turnoId,
                    docenteId,
                    estado);

    byte[] archivo =
            ExcelExporter.exportar(datos);

    return ResponseEntity.ok()

            .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=Reporte_Asistencias.xlsx")

            .contentType(
                    MediaType.APPLICATION_OCTET_STREAM)

            .body(archivo);
}
@GetMapping("/estadisticas")
public ResponseEntity<EstadisticasDTO> estadisticas(

        @RequestParam String fecha) {

    return ResponseEntity.ok(

            asistenciaService.obtenerEstadisticas(

                    LocalDate.parse(fecha)));
}
@GetMapping("/estado-general")
public ResponseEntity<EstadoGeneralJornadaDTO> estadoGeneral(
        @RequestParam String fecha) {

    return ResponseEntity.ok(
            asistenciaService.obtenerEstadoGeneralJornada(
                    LocalDate.parse(fecha)));
}

@GetMapping("/indicadores")
public ResponseEntity<IndicadoresInstitucionalesDTO> obtenerIndicadores() {

    return ResponseEntity.ok(

            asistenciaService.obtenerIndicadoresInstitucionales()

    );

}
@GetMapping("/evolucion")
public ResponseEntity<List<EvolucionAsistenciaDTO>> obtenerEvolucion() {
    LocalDate hasta = LocalDate.now();
    LocalDate desde = hasta.minusDays(29);
    return ResponseEntity.ok(

            asistenciaService.obtenerEvolucion(desde,hasta)

    );

}
@GetMapping("/resumen-ejecutivo")
public ResponseEntity<ResumenEjecutivoDTO> obtenerResumenEjecutivo(

        @RequestParam String fecha) {

    return ResponseEntity.ok(

            asistenciaService.obtenerResumenEjecutivo(

                    LocalDate.parse(fecha)

            )

    );

}

@GetMapping("/comparacion-historica")
public ResponseEntity<ComparacionHistoricaDTO> comparacionHistorica(

        @RequestParam String fecha) {

    return ResponseEntity.ok(

            asistenciaService.obtenerComparacionHistorica(

                    LocalDate.parse(fecha)));

}
@GetMapping("/estadisticas-turno")
public ResponseEntity<EstadisticasTurnoDTO> estadisticasPorTurno(

        @RequestParam String fecha,

        @RequestParam Long turnoId) {

    return ResponseEntity.ok(

            asistenciaService.obtenerEstadisticasPorTurno(

                    LocalDate.parse(fecha),

                    turnoId));

}
}
