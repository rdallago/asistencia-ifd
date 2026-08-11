
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
import com.lowagie.text.PageSize;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.stereotype.Service;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.time.LocalDate;

import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

@Service
public class ReportePdfService {

    private final AsistenciaService asistenciaService;

    public ReportePdfService(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }


    // =====================================================
    // REPORTE EJECUTIVO INSTITUCIONAL
    // =====================================================

    public byte[] generarReporteEjecutivo(LocalDate fecha) {

        try {

            // =====================================================
            // DATOS
            // =====================================================

            EstadoGeneralJornadaDTO estado =
                    asistenciaService.obtenerEstadoGeneralJornada(fecha);

            EstadisticasDTO estadisticas =
                    asistenciaService.obtenerEstadisticas(fecha);

            IndicadoresInstitucionalesDTO indicadores =
                    asistenciaService.obtenerIndicadoresInstitucionales();

            ComparacionHistoricaDTO comparacion =
                    asistenciaService.obtenerComparacionHistorica(fecha);

            ResumenEjecutivoDTO resumen =
                    asistenciaService.obtenerResumenEjecutivo(fecha);

            List<EvolucionAsistenciaDTO> evolucion =
                    asistenciaService.obtenerEvolucion(
                            fecha.minusDays(29),
                            fecha
                    );

            List<Asistencia> asistenciasReporte =
                    asistenciaService.obtenerAsistenciasParaReporte(fecha);


            // =====================================================
            // DOCUMENTO
            // =====================================================

            ByteArrayOutputStream baos =
                    new ByteArrayOutputStream();

            Document document =
                    new Document(
                            PageSize.A4,
                            36,
                            36,
                            36,
                            55
                    );

            PdfWriter writer =
                    PdfWriter.getInstance(
                            document,
                            baos
                    );

            configurarPiePagina(
                    writer,
                    "REPORTE EJECUTIVO INSTITUCIONAL"
            );

            document.open();


            // =====================================================
            // FUENTES
            // =====================================================

            Font titulo =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            18
                    );

            Font subtitulo =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            11
                    );

            Font seccion =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            14
                    );

            Font normal =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            10
                    );

            Font normalBold =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            10
                    );


            // =====================================================
            // ENCABEZADO INSTITUCIONAL
            // =====================================================

            agregarEncabezadoInstitucional(
                    document,
                    "REPORTE EJECUTIVO INSTITUCIONAL",
                    fecha,
                    "Informe ejecutivo de indicadores institucionales",
                    titulo,
                    subtitulo,
                    normal
            );

            document.add(
                    new Paragraph(" ", normal)
            );


            // =====================================================
            // 1. CALIDAD DEL REGISTRO DIARIO
            // =====================================================

            Paragraph tituloSeccion1 =
                    new Paragraph(
                            "1. CALIDAD DEL REGISTRO DIARIO",
                            seccion
                    );

            tituloSeccion1.setSpacingAfter(10);

            document.add(tituloSeccion1);

            PdfPTable tablaCalidad =
                    new PdfPTable(2);

            tablaCalidad.setWidthPercentage(100);

            tablaCalidad.setWidths(
                    new float[]{40, 60}
            );

            tablaCalidad.setHeaderRows(1);

            tablaCalidad.addCell(
                    encabezado("Indicador")
            );

            tablaCalidad.addCell(
                    encabezado("Valor")
            );

            agregarFila(
                    tablaCalidad,
                    "Estado",
                    valorSeguro(estado.getEstado()),
                    normalBold,
                    normal
            );

            agregarFila(
                    tablaCalidad,
                    "Calidad del registro",
                    estado.getPuntaje() + "%",
                    normalBold,
                    normal
            );

            agregarFila(
                    tablaCalidad,
                    "Registros completos",
                    estado.getRegistrosCompletos()
                            + " de "
                            + estado.getRegistrosIniciados(),
                    normalBold,
                    normal
            );

            agregarFila(
                    tablaCalidad,
                    "Descripción",
                    valorSeguro(estado.getDescripcion()),
                    normalBold,
                    normal
            );

            document.add(tablaCalidad);

            document.add(
                    new Paragraph(" ", normal)
            );


            // =====================================================
            // 2. INDICADORES DIARIOS
            // =====================================================

            Paragraph tituloSeccion2 =
                    new Paragraph(
                            "2. INDICADORES DIARIOS",
                            seccion
                    );

            tituloSeccion2.setSpacingAfter(10);

            document.add(tituloSeccion2);

            PdfPTable tablaIndicadores =
                    new PdfPTable(2);

            tablaIndicadores.setWidthPercentage(100);

            tablaIndicadores.setWidths(
                    new float[]{60, 40}
            );

            tablaIndicadores.setHeaderRows(1);

            tablaIndicadores.addCell(
                    encabezado("Indicador")
            );

            tablaIndicadores.addCell(
                    encabezado("Valor")
            );

            agregarFila(
                    tablaIndicadores,
                    "Actividad registrada",
                    String.valueOf(
                            estadisticas.getActividadRegistrada()
                    ),
                    normalBold,
                    normal
            );

            agregarFila(
                    tablaIndicadores,
                    "Registros completos",
                    String.valueOf(
                            estadisticas.getPresentes()
                    ),
                    normalBold,
                    normal
            );

            agregarFila(
                    tablaIndicadores,
                    "Pendientes de egreso",
                    String.valueOf(
                            estadisticas.getSinEgreso()
                    ),
                    normalBold,
                    normal
            );

            agregarFila(
                    tablaIndicadores,
                    "Sin registro",
                    String.valueOf(
                            estadisticas.getSinFirmaTotal()
                    ),
                    normalBold,
                    normal
            );

            agregarFila(
                    tablaIndicadores,
                    "Porcentaje de asistencia",
                    String.format(
                            Locale.US,
                            "%.2f%%",
                            estadisticas.getPorcentajeAsistencia()
                    ).replace('.', ','),
                    normalBold,
                    normal
            );

            document.add(tablaIndicadores);

            document.add(
                    new Paragraph(" ", normal)
            );


            // =====================================================
            // 3. INDICADORES INSTITUCIONALES
            // =====================================================

            Paragraph tituloSeccion3 =
                    new Paragraph(
                            "3. INDICADORES INSTITUCIONALES",
                            seccion
                    );

            tituloSeccion3.setSpacingAfter(10);

            document.add(tituloSeccion3);

            PdfPTable tablaInstitucional =
                    new PdfPTable(2);

            tablaInstitucional.setWidthPercentage(100);

            tablaInstitucional.setWidths(
                    new float[]{70, 30}
            );

            tablaInstitucional.setHeaderRows(1);

            tablaInstitucional.addCell(
                    encabezado("Indicador")
            );

            tablaInstitucional.addCell(
                    encabezado("Valor")
            );

            agregarFila(
                    tablaInstitucional,
                    "Docentes registrados",
                    String.valueOf(
                            indicadores.getDocentesRegistrados()
                    ),
                    normalBold,
                    normal
            );

            agregarFila(
                    tablaInstitucional,
                    "Docentes activos",
                    String.valueOf(
                            indicadores.getDocentesActivos()
                    ),
                    normalBold,
                    normal
            );

            agregarFila(
                    tablaInstitucional,
                    "Turnos configurados",
                    String.valueOf(
                            indicadores.getTurnosConfigurados()
                    ),
                    normalBold,
                    normal
            );

            agregarFila(
                    tablaInstitucional,
                    "Asignaciones vigentes",
                    String.valueOf(
                            indicadores.getAsignacionesVigentes()
                    ),
                    normalBold,
                    normal
            );

            agregarFila(
                    tablaInstitucional,
                    "Jornadas abiertas",
                    String.valueOf(
                            indicadores.getJornadasAbiertas()
                    ),
                    normalBold,
                    normal
            );

            document.add(tablaInstitucional);

            document.add(
                    new Paragraph(" ", normal)
            );


            // =====================================================
            // 4. COMPARACIÓN HISTÓRICA
            // =====================================================

            Paragraph tituloSeccion4 =
                    new Paragraph(
                            "4. COMPARACIÓN HISTÓRICA",
                            seccion
                    );

            tituloSeccion4.setSpacingAfter(10);

            document.add(tituloSeccion4);

            PdfPTable tablaComparacion =
                    new PdfPTable(2);

            tablaComparacion.setWidthPercentage(100);

            tablaComparacion.setWidths(
                    new float[]{70, 30}
            );

            tablaComparacion.setHeaderRows(1);

            tablaComparacion.addCell(
                    encabezado("Indicador")
            );

            tablaComparacion.addCell(
                    encabezado("Valor")
            );

            agregarFila(
                    tablaComparacion,
                    "Promedio histórico (últimos 30 días)",
                    String.format(
                            Locale.US,
                            "%.2f%%",
                            comparacion.getPromedioHistorico()
                    ).replace('.', ','),
                    normalBold,
                    normal
            );

            agregarFila(
                    tablaComparacion,
                    "Valor actual",
                    String.format(
                            Locale.US,
                            "%.2f%%",
                            comparacion.getValorActual()
                    ).replace('.', ','),
                    normalBold,
                    normal
            );

            agregarFila(
                    tablaComparacion,
                    "Variación respecto del promedio",
                    String.format(
                            Locale.US,
                            "%+.2f",
                            comparacion.getDiferencia()
                    ).replace('.', ',')
                            + " puntos porcentuales",
                    normalBold,
                    normal
            );

            agregarFila(
                    tablaComparacion,
                    "Tendencia",
                    valorSeguro(
                            comparacion.getTendencia()
                    ),
                    normalBold,
                    normal
            );

            document.add(tablaComparacion);

            document.add(
                    new Paragraph(" ", normal)
            );


            // =====================================================
            // 5. RESUMEN EJECUTIVO
            // =====================================================

            Paragraph tituloSeccion5 =
                    new Paragraph(
                            "5. RESUMEN EJECUTIVO",
                            seccion
                    );

            tituloSeccion5.setSpacingAfter(10);

            document.add(tituloSeccion5);

            PdfPTable tablaResumen =
                    new PdfPTable(2);

            tablaResumen.setWidthPercentage(100);

            tablaResumen.setWidths(
                    new float[]{30, 70}
            );

            tablaResumen.setHeaderRows(1);

            tablaResumen.addCell(
                    encabezado("Sección")
            );

            tablaResumen.addCell(
                    encabezado("Información")
            );

            String estadoGeneralTexto =
                    resumen.getEstadoGeneral();

            if (estadoGeneralTexto == null
                    || estadoGeneralTexto.trim().isEmpty()) {

                estadoGeneralTexto =
                        "Sin información disponible.";
            }

            agregarFila(
                    tablaResumen,
                    "Estado general",
                    estadoGeneralTexto,
                    normalBold,
                    normal
            );

            String tendenciasTexto =
                    resumen.getTendencias() == null
                            || resumen.getTendencias().isEmpty()
                            ? "Sin información disponible."
                            : String.join(
                                    "\n• ",
                                    resumen.getTendencias()
                            );

            agregarFila(
                    tablaResumen,
                    "Tendencias",
                    "• " + tendenciasTexto,
                    normalBold,
                    normal
            );

            String observacionesTexto =
                    resumen.getObservaciones() == null
                            || resumen.getObservaciones().isEmpty()
                            ? "Sin observaciones."
                            : String.join(
                                    "\n• ",
                                    resumen.getObservaciones()
                            );

            agregarFila(
                    tablaResumen,
                    "Observaciones",
                    "• " + observacionesTexto,
                    normalBold,
                    normal
            );

            String recomendacionesTexto =
                    resumen.getRecomendaciones() == null
                            || resumen.getRecomendaciones().isEmpty()
                            ? "Sin recomendaciones."
                            : String.join(
                                    "\n• ",
                                    resumen.getRecomendaciones()
                            );

            agregarFila(
                    tablaResumen,
                    "Recomendaciones",
                    "• " + recomendacionesTexto,
                    normalBold,
                    normal
            );

            String conclusionesTexto =
                    resumen.getConclusiones() == null
                            || resumen.getConclusiones().isEmpty()
                            ? "Sin conclusión disponible."
                            : String.join(
                                    "\n• ",
                                    resumen.getConclusiones()
                            );

            agregarFila(
                    tablaResumen,
                    "Conclusión",
                    "• " + conclusionesTexto,
                    normalBold,
                    normal
            );

            document.add(tablaResumen);

            document.add(
                    new Paragraph(" ", normal)
            );


            // =====================================================
            // 6. EVOLUCIÓN HISTÓRICA
            // =====================================================

            Paragraph tituloSeccion6 =
                    new Paragraph(
                            "6. EVOLUCIÓN HISTÓRICA DE LA ASISTENCIA",
                            seccion
                    );

            tituloSeccion6.setSpacingAfter(10);

            document.add(tituloSeccion6);

            PdfPTable tablaEvolucion =
                    new PdfPTable(4);

            tablaEvolucion.setWidthPercentage(100);

            tablaEvolucion.setWidths(
                    new float[]{25, 25, 25, 25}
            );

            tablaEvolucion.setHeaderRows(1);

            tablaEvolucion.addCell(
                    encabezado("Fecha")
            );

            tablaEvolucion.addCell(
                    encabezado("Presentes")
            );

            tablaEvolucion.addCell(
                    encabezado("Sin egreso")
            );

            tablaEvolucion.addCell(
                    encabezado("Sin firma")
            );

            if (evolucion == null || evolucion.isEmpty()) {

                PdfPCell celdaSinDatos =
                        new PdfPCell(
                                new Phrase(
                                        "No existen datos históricos disponibles.",
                                        normal
                                )
                        );

                celdaSinDatos.setColspan(4);
                celdaSinDatos.setPadding(8);

                tablaEvolucion.addCell(
                        celdaSinDatos
                );

            } else {

                for (EvolucionAsistenciaDTO e : evolucion) {

                    tablaEvolucion.addCell(
                            new Phrase(
                                    e.getFecha() != null
                                            ? e.getFecha().toString()
                                            : "-",
                                    normal
                            )
                    );

                    tablaEvolucion.addCell(
                            new Phrase(
                                    String.valueOf(
                                            e.getPresentes()
                                    ),
                                    normal
                            )
                    );

                    tablaEvolucion.addCell(
                            new Phrase(
                                    String.valueOf(
                                            e.getSinEgreso()
                                    ),
                                    normal
                            )
                    );

                    tablaEvolucion.addCell(
                            new Phrase(
                                    String.valueOf(
                                            e.getSinFirmaTotal()
                                    ),
                                    normal
                            )
                    );
                }
            }

            document.add(tablaEvolucion);

            document.add(
                    new Paragraph(" ", normal)
            );


            // =====================================================
            // GRÁFICO DE EVOLUCIÓN
            // =====================================================

            if (evolucion != null
                    && !evolucion.isEmpty()) {

                Image grafico =
                        generarGraficoEvolucion(
                                evolucion
                        );

                grafico.scaleToFit(
                        500,
                        250
                );

                grafico.setAlignment(
                        Element.ALIGN_CENTER
                );

                document.add(grafico);

                document.add(
                        new Paragraph(" ", normal)
                );
            }


            // =====================================================
            // 7. DETALLE DE ASISTENCIA DEL DÍA
            // =====================================================

            Paragraph tituloSeccion7 =
                    new Paragraph(
                            "7. DETALLE DE ASISTENCIA DEL DÍA",
                            seccion
                    );

            tituloSeccion7.setSpacingAfter(10);

            document.add(tituloSeccion7);

            PdfPTable tablaDetalle =
                    new PdfPTable(6);

            tablaDetalle.setWidthPercentage(100);

            tablaDetalle.setWidths(
                    new float[]{
                            3f,
                            6f,
                            3f,
                            2f,
                            2f,
                            3f
                    }
            );

            tablaDetalle.setHeaderRows(1);

            tablaDetalle.addCell(
                    encabezado("DNI")
            );

            tablaDetalle.addCell(
                    encabezado("Docente")
            );

            tablaDetalle.addCell(
                    encabezado("Turno")
            );

            tablaDetalle.addCell(
                    encabezado("Ingreso")
            );

            tablaDetalle.addCell(
                    encabezado("Egreso")
            );

            tablaDetalle.addCell(
                    encabezado("Estado")
            );

            if (asistenciasReporte == null
                    || asistenciasReporte.isEmpty()) {

                PdfPCell celdaSinDatos =
                        new PdfPCell(
                                new Phrase(
                                        "No existen registros de asistencia "
                                                + "para la fecha seleccionada.",
                                        normal
                                )
                        );

                celdaSinDatos.setColspan(6);
                celdaSinDatos.setPadding(8);

                tablaDetalle.addCell(
                        celdaSinDatos
                );

            } else {

                for (Asistencia a : asistenciasReporte) {

                    String dni =
                            a.getDocente() != null
                                    && a.getDocente().getDni() != null
                                    ? a.getDocente().getDni()
                                    : "-";

                    String docente =
                            a.getDocente() != null
                                    ? valorSeguro(
                                            a.getDocente().getApellido()
                                    )
                                    + ", "
                                    + valorSeguro(
                                            a.getDocente().getNombre()
                                    )
                                    : "-";

                    String turno =
                            a.getTurno() != null
                                    ? valorSeguro(
                                            a.getTurno().getNombre()
                                    )
                                    : "-";

                    String ingreso =
                            a.getHoraIngreso() != null
                                    ? a.getHoraIngreso().toString()
                                    : "-";

                    String egreso =
                            a.getHoraEgreso() != null
                                    ? a.getHoraEgreso().toString()
                                    : "-";

                    tablaDetalle.addCell(
                            new Phrase(
                                    dni,
                                    normal
                            )
                    );

                    tablaDetalle.addCell(
                            new Phrase(
                                    docente,
                                    normal
                            )
                    );

                    tablaDetalle.addCell(
                            new Phrase(
                                    turno,
                                    normal
                            )
                    );

                    tablaDetalle.addCell(
                            new Phrase(
                                    ingreso,
                                    normal
                            )
                    );

                    tablaDetalle.addCell(
                            new Phrase(
                                    egreso,
                                    normal
                            )
                    );

                    tablaDetalle.addCell(
                            new Phrase(
                                    obtenerTextoEstado(
                                            a.getEstado()
                                    ),
                                    normal
                            )
                    );
                }
            }

            document.add(tablaDetalle);

            document.add(
                    new Paragraph(" ", normal)
            );


            // =====================================================
            // 8. CIERRE DEL INFORME
            // =====================================================

            Paragraph tituloSeccion8 =
                    new Paragraph(
                            "8. CIERRE DEL INFORME",
                            seccion
                    );

            tituloSeccion8.setSpacingAfter(10);

            document.add(tituloSeccion8);

            Paragraph textoFinal =
                    new Paragraph(
                            "El presente informe resume los principales "
                                    + "indicadores de asistencia correspondientes "
                                    + "a la fecha seleccionada, incluyendo la calidad "
                                    + "del registro diario, los indicadores de asistencia, "
                                    + "la situación institucional, la comparación con "
                                    + "el comportamiento histórico, el resumen ejecutivo "
                                    + "y la evolución de los registros. El detalle de "
                                    + "asistencia permite identificar los registros "
                                    + "completos y aquellos que requieren seguimiento "
                                    + "administrativo.",
                            normal
                    );

            textoFinal.setSpacingAfter(12);

            document.add(textoFinal);


            // =====================================================
            // CIERRE PDF
            // =====================================================

            document.close();

            return baos.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error al generar el reporte PDF",
                    e
            );
        }
    }


    // =====================================================
    // REPORTE DE DOCENTES
    // =====================================================

    public byte[] generarReporteDocentes(
            List<Docente> docentes) {

        try {

            ByteArrayOutputStream baos =
                    new ByteArrayOutputStream();

            Document document =
                    new Document(
                            PageSize.A4,
                            36,
                            36,
                            36,
                            55
                    );

            PdfWriter writer =
                    PdfWriter.getInstance(
                            document,
                            baos
                    );

            configurarPiePagina(
                    writer,
                    "LISTADO GENERAL DE DOCENTES"
            );

            document.open();

            Font titulo =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            18
                    );

            Font subtitulo =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            11
                    );

            Font normal =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            10
                    );

            agregarEncabezadoInstitucional(
                    document,
                    "LISTADO GENERAL DE DOCENTES",
                    LocalDate.now(),
                    "Cantidad de docentes activos: "
                            + docentes.size(),
                    titulo,
                    subtitulo,
                    normal
            );

            PdfPTable tabla =
                    new PdfPTable(6);

            tabla.setWidthPercentage(100);

            tabla.setWidths(
                    new float[]{
                            2.5f,
                            4f,
                            5f,
                            4f,
                            5f,
                            4f
                    }
            );

            tabla.setHeaderRows(1);

            tabla.addCell(
                    encabezado("DNI")
            );

            tabla.addCell(
                    encabezado("Apellido")
            );

            tabla.addCell(
                    encabezado("Nombre")
            );

            tabla.addCell(
                    encabezado("Cargo")
            );

            tabla.addCell(
                    encabezado("Correo")
            );

            tabla.addCell(
                    encabezado("Teléfono")
            );

            for (Docente d : docentes) {

                tabla.addCell(
                        new Phrase(
                                d.getDni() != null
                                        ? d.getDni()
                                        : "-",
                                normal
                        )
                );

                tabla.addCell(
                        new Phrase(
                                d.getApellido() != null
                                        ? d.getApellido()
                                        : "-",
                                normal
                        )
                );

                tabla.addCell(
                        new Phrase(
                                d.getNombre() != null
                                        ? d.getNombre()
                                        : "-",
                                normal
                        )
                );

                tabla.addCell(
                        new Phrase(
                                d.getCargo() != null
                                        ? d.getCargo()
                                        : "-",
                                normal
                        )
                );

                tabla.addCell(
                        new Phrase(
                                d.getCorreo() != null
                                        ? d.getCorreo()
                                        : "-",
                                normal
                        )
                );

                tabla.addCell(
                        new Phrase(
                                d.getTelefono() != null
                                        ? d.getTelefono()
                                        : "-",
                                normal
                        )
                );
            }

            document.add(tabla);

            agregarCierreDocumento(
                    document,
                    normal
            );

            document.close();

            return baos.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error generando reporte de docentes",
                    e
            );
        }
    }


    // =====================================================
    // REPORTE DE TURNOS
    // =====================================================

    public byte[] generarReporteTurnos(
            List<Turno> turnos) {

        try {

            ByteArrayOutputStream baos =
                    new ByteArrayOutputStream();

            Document document =
                    new Document(
                            PageSize.A4,
                            36,
                            36,
                            36,
                            55
                    );

            PdfWriter writer =
                    PdfWriter.getInstance(
                            document,
                            baos
                    );

            configurarPiePagina(
                    writer,
                    "LISTADO GENERAL DE TURNOS"
            );

            document.open();

            Font titulo =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            18
                    );

            Font subtitulo =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            11
                    );

            Font normal =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            10
                    );

            agregarEncabezadoInstitucional(
                    document,
                    "LISTADO GENERAL DE TURNOS",
                    LocalDate.now(),
                    "Cantidad de turnos activos: "
                            + turnos.size(),
                    titulo,
                    subtitulo,
                    normal
            );

            PdfPTable tabla =
                    new PdfPTable(4);

            tabla.setWidthPercentage(100);

            tabla.setWidths(
                    new float[]{
                            1.5f,
                            5f,
                            4f,
                            4f
                    }
            );

            tabla.setHeaderRows(1);

            tabla.addCell(
                    encabezado("ID")
            );

            tabla.addCell(
                    encabezado("Turno")
            );

            tabla.addCell(
                    encabezado("Hora inicio")
            );

            tabla.addCell(
                    encabezado("Hora fin")
            );

            for (Turno turno : turnos) {

                tabla.addCell(
                        new Phrase(
                                turno.getId() != null
                                        ? turno.getId().toString()
                                        : "-",
                                normal
                        )
                );

                tabla.addCell(
                        new Phrase(
                                turno.getNombre() != null
                                        ? turno.getNombre()
                                        : "-",
                                normal
                        )
                );

                tabla.addCell(
                        new Phrase(
                                turno.getHoraInicio() != null
                                        ? turno.getHoraInicio().toString()
                                        : "-",
                                normal
                        )
                );

                tabla.addCell(
                        new Phrase(
                                turno.getHoraFin() != null
                                        ? turno.getHoraFin().toString()
                                        : "-",
                                normal
                        )
                );
            }

            document.add(tabla);

            agregarCierreDocumento(
                    document,
                    normal
            );

            document.close();

            return baos.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error generando reporte de turnos",
                    e
            );
        }
    }


    // =====================================================
    // ENCABEZADO INSTITUCIONAL
    // =====================================================

    private void agregarEncabezadoInstitucional(
            Document document,
            String titulo,
            LocalDate fecha,
            String cantidadTexto,
            Font tituloFont,
            Font subtituloFont,
            Font normalFont)
            throws Exception {

        InputStream logoStream =
                getClass()
                        .getResourceAsStream(
                                "/static/img/logo-ifd.png"
                        );

        if (logoStream != null) {

            byte[] logoBytes =
                    logoStream.readAllBytes();

            Image logo =
                    Image.getInstance(
                            logoBytes
                    );

            logo.scaleToFit(
                    335,
                    100
            );

            logo.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(logo);
        }


        Paragraph tituloParrafo =
                new Paragraph(
                        titulo,
                        tituloFont
                );

        tituloParrafo.setAlignment(
                Element.ALIGN_CENTER
        );

        tituloParrafo.setSpacingBefore(8);
        tituloParrafo.setSpacingAfter(4);

        document.add(tituloParrafo);


        Paragraph sistema =
                new Paragraph(
                        "Sistema de Gestión de Asistencia",
                        subtituloFont
                );

        sistema.setAlignment(
                Element.ALIGN_CENTER
        );

        document.add(sistema);


        Paragraph instituto =
                new Paragraph(
                        "Instituto Superior de Formación Docente y Técnica",
                        subtituloFont
                );

        instituto.setAlignment(
                Element.ALIGN_CENTER
        );

        document.add(instituto);


        Paragraph nombreInstituto =
                new Paragraph(
                        "\"Dr. J. A. Ferreira\"",
                        subtituloFont
                );

        nombreInstituto.setAlignment(
                Element.ALIGN_CENTER
        );

        document.add(nombreInstituto);


        Paragraph fechaParrafo =
                new Paragraph(
                        "Fecha: " + fecha,
                        normalFont
                );

        fechaParrafo.setAlignment(
                Element.ALIGN_CENTER
        );

        fechaParrafo.setSpacingBefore(3);

        document.add(fechaParrafo);


        Paragraph cantidadParrafo =
                new Paragraph(
                        cantidadTexto,
                        normalFont
                );

        cantidadParrafo.setAlignment(
                Element.ALIGN_CENTER
        );

        document.add(cantidadParrafo);


        document.add(
                new Paragraph(
                        " ",
                        normalFont
                )
        );
    }


    // =====================================================
    // CIERRE DE DOCUMENTOS
    // =====================================================

    private void agregarCierreDocumento(
            Document document,
            Font normalFont) {

        document.add(
                new Paragraph(
                        " ",
                        normalFont
                )
        );

        Paragraph cierre =
                new Paragraph(
                        "Documento generado por el Sistema de Gestión de Asistencia.",
                        normalFont
                );

        cierre.setAlignment(
                Element.ALIGN_CENTER
        );

        document.add(cierre);
    }


    // =====================================================
    // ESTADO DE ASISTENCIA
    // =====================================================

    private String obtenerTextoEstado(
            EstadoAsistencia estado) {

        if (estado == null) {
            return "Sin información";
        }

        switch (estado) {

            case PRESENTE:
                return "Presente";

            case SIN_EGRESO:
                return "Sin egreso";

            case SIN_FIRMA_TOTAL:
                return "Sin registro";

            default:
                return estado.toString();
        }
    }


    // =====================================================
    // ENCABEZADO DE TABLAS
    // =====================================================

    private PdfPCell encabezado(
            String texto) {

        Font fuente =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        10,
                        Color.WHITE
                );

        PdfPCell celda =
                new PdfPCell(
                        new Phrase(
                                texto,
                                fuente
                        )
                );

        celda.setBackgroundColor(
                new Color(
                        13,
                        110,
                        253
                )
        );

        celda.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );

        celda.setVerticalAlignment(
                Element.ALIGN_MIDDLE
        );

        celda.setPadding(7);

        return celda;
    }


    // =====================================================
    // FILA DE TABLA
    // =====================================================

    private void agregarFila(
            PdfPTable tabla,
            String etiqueta,
            String valor,
            Font fuenteEtiqueta,
            Font fuenteValor) {

        PdfPCell celdaEtiqueta =
                new PdfPCell(
                        new Phrase(
                                etiqueta != null
                                        ? etiqueta
                                        : "-",
                                fuenteEtiqueta
                        )
                );

        celdaEtiqueta.setBackgroundColor(
                Color.WHITE
        );

        celdaEtiqueta.setHorizontalAlignment(
                Element.ALIGN_LEFT
        );

        celdaEtiqueta.setVerticalAlignment(
                Element.ALIGN_MIDDLE
        );

        celdaEtiqueta.setPadding(7);


        PdfPCell celdaValor =
                new PdfPCell(
                        new Phrase(
                                valor != null
                                        ? valor
                                        : "-",
                                fuenteValor
                        )
                );

        celdaValor.setBackgroundColor(
                Color.WHITE
        );

        celdaValor.setHorizontalAlignment(
                Element.ALIGN_LEFT
        );

        celdaValor.setVerticalAlignment(
                Element.ALIGN_MIDDLE
        );

        celdaValor.setPadding(7);


        tabla.addCell(celdaEtiqueta);
        tabla.addCell(celdaValor);
    }


    // =====================================================
    // TEXTO SEGURO
    // =====================================================

    private String valorSeguro(
            String valor) {

        if (valor == null
                || valor.trim().isEmpty()) {

            return "-";
        }

        return valor;
    }


    // =====================================================
    // GRÁFICO DE EVOLUCIÓN
    // =====================================================

    private Image generarGraficoEvolucion(
            List<EvolucionAsistenciaDTO> evolucion)
            throws Exception {

        int ancho = 700;
        int alto = 320;

        BufferedImage imagen =
                new BufferedImage(
                        ancho,
                        alto,
                        BufferedImage.TYPE_INT_RGB
                );

        Graphics2D g =
                imagen.createGraphics();


        g.setColor(Color.WHITE);

        g.fillRect(
                0,
                0,
                ancho,
                alto
        );


        g.setColor(Color.DARK_GRAY);

        g.drawString(
                "Evolución de docentes presentes - últimos 30 días",
                30,
                30
        );


        int margenIzquierdo = 55;
        int margenDerecho = 25;
        int margenSuperior = 50;
        int margenInferior = 50;

        int anchoGrafico =
                ancho
                        - margenIzquierdo
                        - margenDerecho;

        int altoGrafico =
                alto
                        - margenSuperior
                        - margenInferior;


        long maxPresentes =
                evolucion.stream()
                        .mapToLong(
                                EvolucionAsistenciaDTO::getPresentes
                        )
                        .max()
                        .orElse(1);

        if (maxPresentes == 0) {
            maxPresentes = 1;
        }


        g.setColor(Color.LIGHT_GRAY);

        g.drawLine(
                margenIzquierdo,
                margenSuperior,
                margenIzquierdo,
                alto - margenInferior
        );

        g.drawLine(
                margenIzquierdo,
                alto - margenInferior,
                ancho - margenDerecho,
                alto - margenInferior
        );


        g.setColor(
                new Color(
                        13,
                        110,
                        253
                )
        );

        g.setStroke(
                new BasicStroke(2.5f)
        );


        int cantidad =
                evolucion.size();

        if (cantidad > 1) {

            for (
                    int i = 0;
                    i < cantidad - 1;
                    i++
            ) {

                long valor1 =
                        evolucion.get(i)
                                .getPresentes();

                long valor2 =
                        evolucion.get(i + 1)
                                .getPresentes();


                int x1 =
                        margenIzquierdo
                                + (
                                i
                                        * anchoGrafico
                                        / (cantidad - 1)
                        );

                int x2 =
                        margenIzquierdo
                                + (
                                (i + 1)
                                        * anchoGrafico
                                        / (cantidad - 1)
                        );


                int y1 =
                        alto
                                - margenInferior
                                - (
                                int
                                        ) (
                                        valor1
                                                * altoGrafico
                                                / (double) maxPresentes
                                );


                int y2 =
                        alto
                                - margenInferior
                                - (
                                int
                                        ) (
                                        valor2
                                                * altoGrafico
                                                / (double) maxPresentes
                                );


                g.drawLine(
                        x1,
                        y1,
                        x2,
                        y2
                );
            }
        }


        g.dispose();


        ByteArrayOutputStream baos =
                new ByteArrayOutputStream();

        ImageIO.write(
                imagen,
                "png",
                baos
        );

        return Image.getInstance(
                baos.toByteArray()
        );
    }


    // =====================================================
    // EVENTO DEL PIE DE PÁGINA
    // =====================================================

    private static class PiePaginaEvent
            extends PdfPageEventHelper {

        private PdfTemplate totalPaginas;

        private final Font fuentePie;

        private final String tituloReporte;


        public PiePaginaEvent(
                String tituloReporte) {

            this.tituloReporte =
                    tituloReporte;

            this.fuentePie =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            8
                    );
        }


        // =================================================
        // CREAR TEMPLATE PARA TOTAL
        // =================================================

        @Override
        public void onOpenDocument(
                PdfWriter writer,
                Document document) {

            totalPaginas =
                    writer.getDirectContent()
                            .createTemplate(
                                    50,
                                    12
                            );
        }


        // =================================================
        // PIE DE CADA PÁGINA
        // =================================================

        @Override
        public void onEndPage(
                PdfWriter writer,
                Document document) {

            PdfContentByte canvas =
                    writer.getDirectContent();


            float margenIzquierdo =
                    document.leftMargin();

            float margenDerecho =
                    document.rightMargin();


            /*
             * El pie queda dentro del margen inferior.
             */
            float posicionY = 20;


            // =============================================
            // IZQUIERDA
            // =============================================

            String textoInstitucional =
                    "Sistema Integral de Asistencia Docente "
                            + "Instituto Superior de Formación Docente "
                            + "y Técnica Dr. J. A. Ferreira";


            canvas.beginText();

            canvas.setFontAndSize(
                    fuentePie.getBaseFont(),
                    8
            );

            canvas.setTextMatrix(
                    margenIzquierdo,
                    posicionY
            );

            canvas.showText(
                    textoInstitucional
            );

            canvas.endText();


            // =============================================
            // DERECHA
            // Página X de
            // =============================================

            String textoPagina =
                    "Página "
                            + writer.getPageNumber()
                            + " de ";


            float anchoPagina =
                    document.getPageSize()
                            .getWidth();


            float anchoTexto =
                    fuentePie.getBaseFont()
                            .getWidthPoint(
                                    textoPagina,
                                    8
                            );


            canvas.beginText();

            canvas.setFontAndSize(
                    fuentePie.getBaseFont(),
                    8
            );

            canvas.setTextMatrix(
                    anchoPagina
                            - margenDerecho
                            - anchoTexto
                            - 20,
                    posicionY
            );

            canvas.showText(
                    textoPagina
            );

            canvas.endText();


            // =============================================
            // TOTAL DE PÁGINAS
            // =============================================

            canvas.addTemplate(
                    totalPaginas,
                    anchoPagina
                            - margenDerecho
                            - 18,
                    posicionY
            );
        }


        // =================================================
        // COMPLETAR TOTAL DE PÁGINAS
        // =================================================

        @Override
        public void onCloseDocument(
                PdfWriter writer,
                Document document) {

            totalPaginas.beginText();

            totalPaginas.setFontAndSize(
                    fuentePie.getBaseFont(),
                    8
            );

            totalPaginas.setTextMatrix(
                    0,
                    0
            );

            /*
             * IMPORTANTE:
             * OpenPDF deja el contador una página por encima
             * en este punto, por eso usamos -1.
             *
             * 1 página -> 1
             * 3 páginas -> 3
             */

            totalPaginas.showText(
                    String.valueOf(
                            writer.getPageNumber() - 1
                    )
            );

            totalPaginas.endText();
        }
    }


    // =====================================================
    // CONFIGURAR PIE DE PÁGINA
    // =====================================================

    private void configurarPiePagina(
            PdfWriter writer,
            String tituloReporte) {

        writer.setPageEvent(
                new PiePaginaEvent(
                        tituloReporte
                )
        );
    }
}
