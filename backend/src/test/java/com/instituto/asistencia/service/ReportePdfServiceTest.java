package com.instituto.asistencia.service;

import com.instituto.asistencia.dto.ComparacionHistoricaDTO;
import com.instituto.asistencia.dto.EstadisticasDTO;
import com.instituto.asistencia.dto.EstadoGeneralJornadaDTO;
import com.instituto.asistencia.dto.EvolucionAsistenciaDTO;
import com.instituto.asistencia.dto.IndicadoresInstitucionalesDTO;
import com.instituto.asistencia.dto.ResumenEjecutivoDTO;
import com.instituto.asistencia.entity.Asistencia;
import com.instituto.asistencia.entity.Docente;
import com.instituto.asistencia.entity.Turno;
import com.instituto.asistencia.entity.enums.EstadoAsistencia;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportePdfServiceTest {

@Mock
private AsistenciaService asistenciaService;

@InjectMocks
private ReportePdfService reportePdfService;


// ============================================================
// MÉTODO AUXILIAR
// ============================================================

private Asistencia crearAsistencia(
        Long id,
        String dni,
        String nombre,
        String apellido,
        String turnoNombre,
        EstadoAsistencia estado) {

    Docente docente = new Docente();

    docente.setId(id);
    docente.setDni(dni);
    docente.setNombre(nombre);
    docente.setApellido(apellido);

    Turno turno = new Turno();

    turno.setId(id);
    turno.setNombre(turnoNombre);

    Asistencia asistencia = new Asistencia();

    asistencia.setId(id);
    asistencia.setDocente(docente);
    asistencia.setTurno(turno);
    asistencia.setEstado(estado);
    asistencia.setHoraIngreso(LocalTime.of(18, 20));

    if (estado == EstadoAsistencia.PRESENTE) {
        asistencia.setHoraEgreso(LocalTime.of(23, 30));
    }

    return asistencia;
}


/**
 * Configura los DTO generales utilizados por
 * generarReporteEjecutivo().
 *
 * El ResumenEjecutivoDTO se configura por separado para
 * evitar stubbings innecesarios de Mockito.
 */
private void mockearDatosEjecutivosBasicos(
        LocalDate fecha) {

    EstadoGeneralJornadaDTO estado =
            mock(EstadoGeneralJornadaDTO.class);

    when(estado.getEstado())
            .thenReturn("CERRADA");

    when(estado.getPuntaje())
            .thenReturn(100);

    when(estado.getRegistrosCompletos())
            .thenReturn(10L);

    when(estado.getRegistrosIniciados())
            .thenReturn(10L);

    when(estado.getDescripcion())
            .thenReturn("Todo correcto");


    EstadisticasDTO estadisticas =
            mock(EstadisticasDTO.class);

    when(estadisticas.getActividadRegistrada())
            .thenReturn(10L);

    when(estadisticas.getPresentes())
            .thenReturn(10L);

    when(estadisticas.getSinEgreso())
            .thenReturn(0L);

    when(estadisticas.getSinFirmaTotal())
            .thenReturn(0L);

    when(estadisticas.getPorcentajeAsistencia())
            .thenReturn(100.0);


    IndicadoresInstitucionalesDTO indicadores =
            mock(IndicadoresInstitucionalesDTO.class);

    when(indicadores.getDocentesRegistrados())
            .thenReturn(10L);

    when(indicadores.getDocentesActivos())
            .thenReturn(10L);

    when(indicadores.getTurnosConfigurados())
            .thenReturn(2L);

    when(indicadores.getAsignacionesVigentes())
            .thenReturn(10L);

    when(indicadores.getJornadasAbiertas())
            .thenReturn(0L);


    ComparacionHistoricaDTO comparacion =
            mock(ComparacionHistoricaDTO.class);

    when(comparacion.getPromedioHistorico())
            .thenReturn(90.0);

    when(comparacion.getValorActual())
            .thenReturn(100.0);

    when(comparacion.getDiferencia())
            .thenReturn(10.0);

    when(comparacion.getTendencia())
            .thenReturn("Mejora");


    when(asistenciaService.obtenerEstadoGeneralJornada(fecha))
            .thenReturn(estado);

    when(asistenciaService.obtenerEstadisticas(fecha))
            .thenReturn(estadisticas);

    when(asistenciaService.obtenerIndicadoresInstitucionales())
            .thenReturn(indicadores);

    when(asistenciaService.obtenerComparacionHistorica(fecha))
            .thenReturn(comparacion);

    when(asistenciaService.obtenerEvolucion(
            fecha.minusDays(29),
            fecha))
            .thenReturn(List.of());

    when(asistenciaService.obtenerAsistenciasParaReporte(fecha))
            .thenReturn(List.of());
}


/**
 * Configura un resumen ejecutivo básico para las pruebas
 * que solamente necesitan que el reporte pueda generarse.
 */
private void mockearResumenEjecutivoBasico(
        LocalDate fecha) {

    ResumenEjecutivoDTO resumen =
            mock(ResumenEjecutivoDTO.class);

    when(resumen.getEstadoGeneral())
            .thenReturn("Correcto");

    when(resumen.getTendencias())
            .thenReturn(List.of());

    when(resumen.getObservaciones())
            .thenReturn(List.of());

    when(resumen.getRecomendaciones())
            .thenReturn(List.of());

    when(resumen.getConclusiones())
            .thenReturn(List.of());

    when(asistenciaService.obtenerResumenEjecutivo(fecha))
            .thenReturn(resumen);
}


// ============================================================
// 1. REPORTE DE DOCENTES
// ============================================================

@Test
void debeGenerarReporteDeDocentesCorrectamente() {

    Docente docente = new Docente();

    docente.setId(1L);
    docente.setDni("30123456");
    docente.setApellido("Pérez");
    docente.setNombre("Juan");
    docente.setCargo("Profesor");
    docente.setCorreo("juan@mail.com");
    docente.setTelefono("3794000000");

    byte[] resultado =
            reportePdfService.generarReporteDocentes(
                    List.of(docente)
            );

    assertNotNull(resultado);
    assertTrue(resultado.length > 0);

    assertEquals('%', (char) resultado[0]);
    assertEquals('P', (char) resultado[1]);
    assertEquals('D', (char) resultado[2]);
    assertEquals('F', (char) resultado[3]);
}


// ============================================================
// 2. DOCENTES - LISTA VACÍA
// ============================================================

@Test
void debeGenerarReporteDeDocentesConListaVacia() {

    byte[] resultado =
            reportePdfService.generarReporteDocentes(
                    List.of()
            );

    assertNotNull(resultado);
    assertTrue(resultado.length > 0);

    assertEquals('%', (char) resultado[0]);
    assertEquals('P', (char) resultado[1]);
    assertEquals('D', (char) resultado[2]);
    assertEquals('F', (char) resultado[3]);
}


// ============================================================
// 3. DOCENTES - DATOS NULOS
// ============================================================

@Test
void debeGenerarReporteDeDocentesConDatosNulos() {

    Docente docente = new Docente();

    docente.setId(1L);
    docente.setDni(null);
    docente.setApellido(null);
    docente.setNombre(null);
    docente.setCargo(null);
    docente.setCorreo(null);
    docente.setTelefono(null);

    byte[] resultado =
            reportePdfService.generarReporteDocentes(
                    List.of(docente)
            );

    assertNotNull(resultado);
    assertTrue(resultado.length > 0);
}


// ============================================================
// 4. REPORTE DE TURNOS - LISTA VACÍA
// ============================================================

@Test
void debeGenerarReporteDeTurnosConListaVacia() {

    byte[] resultado =
            reportePdfService.generarReporteTurnos(
                    List.of()
            );

    assertNotNull(resultado);
    assertTrue(resultado.length > 0);

    assertEquals('%', (char) resultado[0]);
    assertEquals('P', (char) resultado[1]);
    assertEquals('D', (char) resultado[2]);
    assertEquals('F', (char) resultado[3]);
}


// ============================================================
// 5. REPORTE DE TURNOS - DATOS NULOS
// ============================================================

@Test
void debeGenerarReporteDeTurnosConDatosNulos() {

    Turno turno = new Turno();

    turno.setId(null);
    turno.setNombre(null);
    turno.setHoraInicio(null);
    turno.setHoraFin(null);

    byte[] resultado =
            reportePdfService.generarReporteTurnos(
                    List.of(turno)
            );

    assertNotNull(resultado);
    assertTrue(resultado.length > 0);
}


// ============================================================
// 6. REPORTE EJECUTIVO - ESCENARIO NORMAL
// ============================================================

@Test
void debeGenerarReporteEjecutivoCorrectamente() {

    LocalDate fecha =
            LocalDate.of(2026, 8, 11);

    mockearDatosEjecutivosBasicos(fecha);
    mockearResumenEjecutivoBasico(fecha);

    byte[] resultado =
            reportePdfService.generarReporteEjecutivo(fecha);

    assertNotNull(resultado);
    assertTrue(resultado.length > 0);

    assertEquals('%', (char) resultado[0]);
    assertEquals('P', (char) resultado[1]);
    assertEquals('D', (char) resultado[2]);
    assertEquals('F', (char) resultado[3]);

    verify(asistenciaService)
            .obtenerEstadoGeneralJornada(fecha);

    verify(asistenciaService)
            .obtenerEstadisticas(fecha);

    verify(asistenciaService)
            .obtenerIndicadoresInstitucionales();

    verify(asistenciaService)
            .obtenerComparacionHistorica(fecha);

    verify(asistenciaService)
            .obtenerResumenEjecutivo(fecha);

    verify(asistenciaService)
            .obtenerEvolucion(
                    fecha.minusDays(29),
                    fecha
            );

    verify(asistenciaService)
            .obtenerAsistenciasParaReporte(fecha);
}


// ============================================================
// 7. REPORTE EJECUTIVO - EVOLUCIÓN HISTÓRICA CON DATOS
// ============================================================

@Test
void debeGenerarReporteEjecutivoConEvolucionHistorica() {

    LocalDate fecha =
            LocalDate.of(2026, 8, 11);

    mockearDatosEjecutivosBasicos(fecha);
    mockearResumenEjecutivoBasico(fecha);

    EvolucionAsistenciaDTO evolucion1 =
            mock(EvolucionAsistenciaDTO.class);

    when(evolucion1.getFecha())
            .thenReturn(fecha.minusDays(2));

    when(evolucion1.getPresentes())
            .thenReturn(8L);

    when(evolucion1.getSinEgreso())
            .thenReturn(1L);

    when(evolucion1.getSinFirmaTotal())
            .thenReturn(1L);


    EvolucionAsistenciaDTO evolucion2 =
            mock(EvolucionAsistenciaDTO.class);

    when(evolucion2.getFecha())
            .thenReturn(fecha.minusDays(1));

    when(evolucion2.getPresentes())
            .thenReturn(9L);

    when(evolucion2.getSinEgreso())
            .thenReturn(1L);

    when(evolucion2.getSinFirmaTotal())
            .thenReturn(0L);


    when(asistenciaService.obtenerEvolucion(
            fecha.minusDays(29),
            fecha))
            .thenReturn(
                    List.of(
                            evolucion1,
                            evolucion2
                    )
            );


    byte[] resultado =
            reportePdfService.generarReporteEjecutivo(fecha);


    assertNotNull(resultado);
    assertTrue(resultado.length > 0);

    assertEquals('%', (char) resultado[0]);
    assertEquals('P', (char) resultado[1]);
    assertEquals('D', (char) resultado[2]);
    assertEquals('F', (char) resultado[3]);


    verify(asistenciaService)
            .obtenerEvolucion(
                    fecha.minusDays(29),
                    fecha
            );
}


// ============================================================
// 8. REPORTE EJECUTIVO - DETALLE DE ASISTENCIAS
// ============================================================

@Test
void debeGenerarReporteEjecutivoConDetalleDeAsistencias() {

    LocalDate fecha =
            LocalDate.of(2026, 8, 11);

    mockearDatosEjecutivosBasicos(fecha);
    mockearResumenEjecutivoBasico(fecha);


    Asistencia presente =
            crearAsistencia(
                    1L,
                    "30123456",
                    "Juan",
                    "Pérez",
                    "Turno noche",
                    EstadoAsistencia.PRESENTE
            );


    Asistencia sinEgreso =
            crearAsistencia(
                    2L,
                    "30123457",
                    "Ana",
                    "Gómez",
                    "Turno noche",
                    EstadoAsistencia.SIN_EGRESO
            );


    Asistencia sinFirma =
            crearAsistencia(
                    3L,
                    "30123458",
                    "Luis",
                    "Rodríguez",
                    "Turno tarde",
                    EstadoAsistencia.SIN_FIRMA_TOTAL
            );


    when(asistenciaService.obtenerAsistenciasParaReporte(fecha))
            .thenReturn(
                    List.of(
                            presente,
                            sinEgreso,
                            sinFirma
                    )
            );


    byte[] resultado =
            reportePdfService.generarReporteEjecutivo(fecha);


    assertNotNull(resultado);
    assertTrue(resultado.length > 0);


    verify(asistenciaService)
            .obtenerAsistenciasParaReporte(fecha);
}


// ============================================================
// 9. REPORTE EJECUTIVO - DATOS NULOS EN ASISTENCIA
// ============================================================

@Test
void debeGenerarReporteEjecutivoConAsistenciaConDatosNulos() {

    LocalDate fecha =
            LocalDate.of(2026, 8, 11);

    mockearDatosEjecutivosBasicos(fecha);
    mockearResumenEjecutivoBasico(fecha);


    Asistencia asistencia =
            new Asistencia();

    asistencia.setId(1L);
    asistencia.setDocente(null);
    asistencia.setTurno(null);
    asistencia.setEstado(null);
    asistencia.setHoraIngreso(null);
    asistencia.setHoraEgreso(null);


    when(asistenciaService.obtenerAsistenciasParaReporte(fecha))
            .thenReturn(
                    List.of(asistencia)
            );


    byte[] resultado =
            reportePdfService.generarReporteEjecutivo(fecha);


    assertNotNull(resultado);
    assertTrue(resultado.length > 0);


    verify(asistenciaService)
            .obtenerAsistenciasParaReporte(fecha);
}


// ============================================================
// 10. REPORTE EJECUTIVO - EVOLUCIÓN NULA
// ============================================================

@Test
void debeGenerarReporteEjecutivoCuandoLaEvolucionEsNula() {

    LocalDate fecha =
            LocalDate.of(2026, 8, 11);

    mockearDatosEjecutivosBasicos(fecha);
    mockearResumenEjecutivoBasico(fecha);


    when(asistenciaService.obtenerEvolucion(
            fecha.minusDays(29),
            fecha))
            .thenReturn(null);


    byte[] resultado =
            reportePdfService.generarReporteEjecutivo(fecha);


    assertNotNull(resultado);
    assertTrue(resultado.length > 0);


    verify(asistenciaService)
            .obtenerEvolucion(
                    fecha.minusDays(29),
                    fecha
            );
}


// ============================================================
// 11. REPORTE EJECUTIVO - ASISTENCIAS NULAS
// ============================================================

@Test
void debeGenerarReporteEjecutivoCuandoLasAsistenciasSonNulas() {

    LocalDate fecha =
            LocalDate.of(2026, 8, 11);

    mockearDatosEjecutivosBasicos(fecha);
    mockearResumenEjecutivoBasico(fecha);


    when(asistenciaService.obtenerAsistenciasParaReporte(fecha))
            .thenReturn(null);


    byte[] resultado =
            reportePdfService.generarReporteEjecutivo(fecha);


    assertNotNull(resultado);
    assertTrue(resultado.length > 0);


    verify(asistenciaService)
            .obtenerAsistenciasParaReporte(fecha);
}


// ============================================================
// 12. REPORTE EJECUTIVO - RESUMEN COMPLETO
// ============================================================

@Test
void debeGenerarReporteEjecutivoConResumenCompleto() {

    LocalDate fecha =
            LocalDate.of(2026, 8, 11);

    mockearDatosEjecutivosBasicos(fecha);


    ResumenEjecutivoDTO resumen =
            mock(ResumenEjecutivoDTO.class);

    when(resumen.getEstadoGeneral())
            .thenReturn("Situación favorable");

    when(resumen.getTendencias())
            .thenReturn(
                    List.of(
                            "La asistencia presenta una evolución favorable",
                            "Se mantienen registros completos"
                    )
            );

    when(resumen.getObservaciones())
            .thenReturn(
                    List.of(
                            "Se detectó un registro pendiente"
                    )
            );

    when(resumen.getRecomendaciones())
            .thenReturn(
                    List.of(
                            "Realizar seguimiento de egresos"
                    )
            );

    when(resumen.getConclusiones())
            .thenReturn(
                    List.of(
                            "La situación general es satisfactoria"
                    )
            );


    when(asistenciaService.obtenerResumenEjecutivo(fecha))
            .thenReturn(resumen);


    byte[] resultado =
            reportePdfService.generarReporteEjecutivo(fecha);


    assertNotNull(resultado);
    assertTrue(resultado.length > 0);


    verify(asistenciaService)
            .obtenerResumenEjecutivo(fecha);
}


// ============================================================
// 13. ERROR AL OBTENER ESTADO GENERAL
// ============================================================

@Test
void debeLanzarExcepcionCuandoFallaElEstadoGeneral() {

    LocalDate fecha =
            LocalDate.of(2026, 8, 11);


    when(asistenciaService.obtenerEstadoGeneralJornada(fecha))
            .thenThrow(
                    new RuntimeException(
                            "Error obteniendo estado"
                    )
            );


    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> reportePdfService
                            .generarReporteEjecutivo(fecha)
            );


    assertEquals(
            "Error al generar el reporte PDF",
            excepcion.getMessage()
    );

    assertNotNull(excepcion.getCause());

    assertEquals(
            "Error obteniendo estado",
            excepcion.getCause().getMessage()
    );


    verify(asistenciaService)
            .obtenerEstadoGeneralJornada(fecha);

    verifyNoMoreInteractions(asistenciaService);
}


// ============================================================
// 14. REPORTE DE DOCENTES - LISTA NULA
// ============================================================

@Test
void debeLanzarExcepcionCuandoLaListaDeDocentesEsNula() {

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> reportePdfService
                            .generarReporteDocentes(null)
            );


    assertEquals(
            "Error generando reporte de docentes",
            excepcion.getMessage()
    );

    assertNotNull(excepcion.getCause());
}


// ============================================================
// 15. REPORTE DE TURNOS - LISTA NULA
// ============================================================

@Test
void debeLanzarExcepcionCuandoLaListaDeTurnosEsNula() {

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> reportePdfService
                            .generarReporteTurnos(null)
            );


    assertEquals(
            "Error generando reporte de turnos",
            excepcion.getMessage()
    );

    assertNotNull(excepcion.getCause());
}
@Test
void debeMostrarIndicadoresCorrectosEnReporteEjecutivo() throws Exception {

    LocalDate fecha = LocalDate.of(2026, 10, 13);

    // Reutilizamos los datos generales de las pruebas existentes.
    mockearDatosEjecutivosBasicos(fecha);
    mockearResumenEjecutivoBasico(fecha);

    // Simulamos los valores verificados para el 13/10/2026.
    EstadisticasDTO estadisticas =
        asistenciaService.obtenerEstadisticas(fecha);

when(estadisticas.getActividadRegistrada()).thenReturn(6L);
when(estadisticas.getPresentes()).thenReturn(2L);
when(estadisticas.getSinEgreso()).thenReturn(0L);
when(estadisticas.getSinFirmaTotal()).thenReturn(4L);
when(estadisticas.getPorcentajeAsistencia()).thenReturn(33.33);
    byte[] pdf = reportePdfService.generarReporteEjecutivo(fecha);

    assertNotNull(pdf);
    assertTrue(pdf.length > 0);

    PdfReader reader = new PdfReader(pdf);

    try {
        StringBuilder contenido = new StringBuilder();

        for (int pagina = 1; pagina <= reader.getNumberOfPages(); pagina++) {
            contenido.append(
                    PdfTextExtractor.getTextFromPage(reader, pagina)
            ).append("\n");
        }

        String texto = contenido.toString();

        assertTrue(texto.contains("33,33%"),
                "El PDF debe mostrar 33,33% de asistencia");

        assertTrue(texto.contains("Actividad registrada"),
                "Debe incluir el indicador de actividad registrada");

        assertTrue(texto.contains("Sin registro"),
                "Debe incluir el indicador de docentes sin registro");

    } finally {
        reader.close();
    }
}
}
