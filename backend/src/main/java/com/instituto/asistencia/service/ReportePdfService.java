package com.instituto.asistencia.service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;   
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import com.lowagie.text.Image;
import com.lowagie.text.Element;
import java.io.InputStream;
import org.springframework.core.io.ClassPathResource;
import com.instituto.asistencia.dto.EstadoGeneralJornadaDTO;
import com.instituto.asistencia.dto.EstadisticasDTO;
import com.instituto.asistencia.dto.IndicadoresInstitucionalesDTO;
import com.instituto.asistencia.dto.ComparacionHistoricaDTO;
import com.instituto.asistencia.dto.ResumenEjecutivoDTO;

import com.lowagie.text.Image;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import java.io.InputStream;



@Service
public class ReportePdfService {
    
    private final AsistenciaService asistenciaService;

public ReportePdfService(AsistenciaService asistenciaService) {
    this.asistenciaService = asistenciaService;
}

    public byte[] generarReporteEjecutivo(LocalDate fecha) {

    try {

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

        ByteArrayOutputStream baos =
                new ByteArrayOutputStream();

        Document document = new Document();

        PdfWriter.getInstance(document, baos);

        document.open();

        // =====================================================
        // FUENTES
        // =====================================================

        Font titulo =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        18);

        Font subtitulo =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        11);

        Font seccion =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        14);

        Font normal =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        10);

        Font normalBold =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        10);

        // =====================================================
        // ENCABEZADO: TÍTULO + LOGO
        // =====================================================

        PdfPTable encabezado = new PdfPTable(2);

        encabezado.setWidthPercentage(100);

        encabezado.setWidths(new float[]{75, 25});

        // ---- IZQUIERDA ----

        PdfPCell celdaTitulo = new PdfPCell();

        celdaTitulo.setBorder(Rectangle.NO_BORDER);

        celdaTitulo.addElement(
                new Paragraph(
                        "REPORTE EJECUTIVO DE INDICADORES",
                        titulo));

        celdaTitulo.addElement(
                new Paragraph(
                        "Sistema de Gestión de Asistencia",
                        subtitulo));

        celdaTitulo.addElement(
                new Paragraph(
                        "Instituto Superior de Formación Docente y Técnica",
                        subtitulo));

        celdaTitulo.addElement(
                new Paragraph(
                        "\"Dr. J. A. Ferreira\"",
                        subtitulo));

        celdaTitulo.addElement(
                new Paragraph(
                        "Fecha: " + fecha,
                        normal));

        encabezado.addCell(celdaTitulo);

        // ---- DERECHA: LOGO ----

        PdfPCell celdaLogo = new PdfPCell();

        celdaLogo.setBorder(Rectangle.NO_BORDER);

        celdaLogo.setHorizontalAlignment(Element.ALIGN_RIGHT);

        celdaLogo.setVerticalAlignment(Element.ALIGN_TOP);

        InputStream logoStream =
                getClass()
                        .getResourceAsStream(
                                "/static/img/logo-ifd.png");

        if (logoStream != null) {

            byte[] logoBytes =
                    logoStream.readAllBytes();

            Image logo =
                    Image.getInstance(logoBytes);

            logo.scaleToFit(100, 100);

            logo.setAlignment(Element.ALIGN_RIGHT);

            celdaLogo.addElement(logo);

        }

        encabezado.addCell(celdaLogo);

        document.add(encabezado);

        document.add(
                new Paragraph(" ", normal));

        // =====================================================
        // 1. CALIDAD DEL REGISTRO DIARIO
        // =====================================================

        document.add(
                new Paragraph(
                        "1. CALIDAD DEL REGISTRO DIARIO",
                        seccion));

        document.add(
                new Paragraph(" ", normal));

        PdfPTable tablaCalidad =
                new PdfPTable(2);

        tablaCalidad.setWidthPercentage(100);

        tablaCalidad.setWidths(
                new float[]{40, 60});

        agregarFila(
                tablaCalidad,
                "Estado",
                estado.getEstado(),
                normalBold,
                normal);

        agregarFila(
                tablaCalidad,
                "Calidad del registro",
                estado.getPuntaje() + "%",
                normalBold,
                normal);

        agregarFila(
                tablaCalidad,
                "Registros completos",
                estado.getRegistrosCompletos()
                        + " de "
                        + estado.getRegistrosIniciados(),
                normalBold,
                normal);

        agregarFila(
                tablaCalidad,
                "Descripción",
                estado.getDescripcion(),
                normalBold,
                normal);

        document.add(tablaCalidad);

        document.add(
                new Paragraph(" ", normal));

        // =====================================================
        // 2. INDICADORES DIARIOS
        // =====================================================

        document.add(
                new Paragraph(
                        "2. INDICADORES DIARIOS",
                        seccion));

        document.add(
                new Paragraph(" ", normal));

        PdfPTable tablaIndicadores =
                new PdfPTable(2);

        tablaIndicadores.setWidthPercentage(100);

        tablaIndicadores.setWidths(
                new float[]{60, 40});

        agregarFila(
                tablaIndicadores,
                "Actividad registrada",
                String.valueOf(
                        estadisticas.getActividadRegistrada()),
                normalBold,
                normal);

        agregarFila(
                tablaIndicadores,
                "Registros completos",
                String.valueOf(
                        estadisticas.getPresentes()),
                normalBold,
                normal);

        agregarFila(
                tablaIndicadores,
                "Pendientes de egreso",
                String.valueOf(
                        estadisticas.getSinEgreso()),
                normalBold,
                normal);

        agregarFila(
                tablaIndicadores,
                "Sin registro",
                String.valueOf(
                        estadisticas.getSinFirmaTotal()),
                normalBold,
                normal);

        agregarFila(
        tablaIndicadores,
        "Porcentaje de asistencia",
        String.format(
                java.util.Locale.US,
                "%.2f%%",
                estadisticas.getPorcentajeAsistencia()
        ).replace('.', ','),
        normalBold,
        normal);
        document.add(tablaIndicadores);

        document.add(
                new Paragraph(" ", normal));
        
        
        // =====================================================
// 3. INDICADORES INSTITUCIONALES
// =====================================================

document.add(
        new Paragraph(
                "3. INDICADORES INSTITUCIONALES",
                seccion));

document.add(
        new Paragraph(" ", normal));

PdfPTable tablaInstitucional =
        new PdfPTable(2);

tablaInstitucional.setWidthPercentage(100);

tablaInstitucional.setWidths(
        new float[]{70, 30});

agregarFila(
        tablaInstitucional,
        "Docentes registrados",
        String.valueOf(
                indicadores.getDocentesRegistrados()),
        normalBold,
        normal);

agregarFila(
        tablaInstitucional,
        "Docentes activos",
        String.valueOf(
                indicadores.getDocentesActivos()),
        normalBold,
        normal);

agregarFila(
        tablaInstitucional,
        "Turnos configurados",
        String.valueOf(
                indicadores.getTurnosConfigurados()),
        normalBold,
        normal);

agregarFila(
        tablaInstitucional,
        "Asignaciones vigentes",
        String.valueOf(
                indicadores.getAsignacionesVigentes()),
        normalBold,
        normal);

agregarFila(
        tablaInstitucional,
        "Jornadas abiertas",
        String.valueOf(
                indicadores.getJornadasAbiertas()),
        normalBold,
        normal);

document.add(tablaInstitucional);

document.add(
        new Paragraph(" ", normal));

// ==========================================
// 4. COMPARACIÓN HISTÓRICA
// ==========================================

Font tituloComparacion =
        FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                14
        );

// =====================================================
// 4. COMPARACIÓN HISTÓRICA
// =====================================================

// Tabla interna de comparación
PdfPTable tablaComparacion = new PdfPTable(2);

tablaComparacion.setWidthPercentage(100);

tablaComparacion.setWidths(
        new float[]{70, 30}
);

agregarFila(
        tablaComparacion,
        "Promedio histórico (últimos 30 días)",
        String.format(
                java.util.Locale.US,
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
                java.util.Locale.US,
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
                java.util.Locale.US,
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
        comparacion.getTendencia(),
        normalBold,
        normal
);


// =====================================================
// BLOQUE ÚNICO: TÍTULO + TABLA
// =====================================================

PdfPTable bloqueComparacion = new PdfPTable(1);

bloqueComparacion.setWidthPercentage(100);
bloqueComparacion.setKeepTogether(true);

PdfPCell celdaComparacion = new PdfPCell();

celdaComparacion.setBorder(PdfPCell.NO_BORDER);
celdaComparacion.setPadding(0);

// Título
Paragraph tituloComp = new Paragraph(
        "4. COMPARACIÓN HISTÓRICA",
        tituloComparacion
);

tituloComp.setSpacingAfter(10);

celdaComparacion.addElement(tituloComp);

// Tabla
celdaComparacion.addElement(tablaComparacion);

// Agregamos todo como un único bloque
bloqueComparacion.addCell(celdaComparacion);

document.add(bloqueComparacion);

document.add(new Paragraph(" ", normal));

Font tituloResumen =
        FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                14
        );

// =====================================================
// 5. RESUMEN EJECUTIVO
// =====================================================

// Tabla del resumen
PdfPTable tablaResumen = new PdfPTable(2);

tablaResumen.setWidthPercentage(100);

tablaResumen.setWidths(
        new float[]{30, 70}
);


// -----------------------------------------
// Estado general
// -----------------------------------------

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


// -----------------------------------------
// Tendencias
// -----------------------------------------

String tendenciasTexto =
        resumen.getTendencias().isEmpty()
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


// -----------------------------------------
// Observaciones
// -----------------------------------------

String observacionesTexto =
        resumen.getObservaciones().isEmpty()
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


// -----------------------------------------
// Recomendaciones
// -----------------------------------------

String recomendacionesTexto =
        resumen.getRecomendaciones().isEmpty()
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


// -----------------------------------------
// Conclusión
// -----------------------------------------

String conclusionesTexto =
        resumen.getConclusiones().isEmpty()
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


// =====================================================
// BLOQUE ÚNICO: TÍTULO + TABLA
// =====================================================

PdfPTable bloqueResumen = new PdfPTable(1);

bloqueResumen.setWidthPercentage(100);

bloqueResumen.setKeepTogether(true);

PdfPCell celdaResumen = new PdfPCell();

celdaResumen.setBorder(PdfPCell.NO_BORDER);

celdaResumen.setPadding(0);


// Título
Paragraph tituloRes =
        new Paragraph(
                "5. RESUMEN EJECUTIVO",
                tituloResumen
        );

tituloRes.setSpacingAfter(10);

celdaResumen.addElement(tituloRes);


// Tabla
celdaResumen.addElement(tablaResumen);


// Agregar bloque completo
bloqueResumen.addCell(celdaResumen);

document.add(bloqueResumen);





        document.close();

        return baos.toByteArray();

    } catch (Exception e) {

        throw new RuntimeException(
                "Error al generar el reporte PDF",
                e);

    }
}
    private void agregarFila(
        PdfPTable tabla,
        String etiqueta,
        String valor,
        Font fuenteEtiqueta,
        Font fuenteValor) {

    PdfPCell celdaEtiqueta =
            new PdfPCell(
                    new Phrase(
                            etiqueta,
                            fuenteEtiqueta));

    PdfPCell celdaValor =
            new PdfPCell(
                    new Phrase(
                            valor,
                            fuenteValor));

    celdaEtiqueta.setPadding(8);
    celdaValor.setPadding(8);

    tabla.addCell(celdaEtiqueta);
    tabla.addCell(celdaValor);
}

}