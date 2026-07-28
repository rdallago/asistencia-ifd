package com.instituto.asistencia.service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;   
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import com.instituto.asistencia.dto.EstadoGeneralJornadaDTO;
import com.instituto.asistencia.dto.EstadisticasDTO;
import com.instituto.asistencia.dto.IndicadoresInstitucionalesDTO;
import com.instituto.asistencia.dto.ComparacionHistoricaDTO;
import com.instituto.asistencia.dto.ResumenEjecutivoDTO;
import com.instituto.asistencia.dto.EvolucionAsistenciaDTO;
import java.util.List;
import com.lowagie.text.Image;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.io.InputStream;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import com.instituto.asistencia.entity.Asistencia;
import com.instituto.asistencia.entity.enums.EstadoAsistencia;

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

// =====================================================
// 6. EVOLUCIÓN HISTÓRICA DE LA ASISTENCIA
// =====================================================

Font tituloEvolucion =
        FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                14
        );

LocalDate desdeEvolucion =
        fecha.minusDays(29);

List<EvolucionAsistenciaDTO> evolucion =
        asistenciaService.obtenerEvolucion(
                desdeEvolucion,
                fecha
        );

// =====================================================
// TABLA DE EVOLUCIÓN
// =====================================================

PdfPTable tablaEvolucion =
        new PdfPTable(4);

tablaEvolucion.setWidthPercentage(100);

tablaEvolucion.setWidths(
        new float[]{25, 25, 25, 25}
);

tablaEvolucion.setHeaderRows(1);

// Encabezados
tablaEvolucion.addCell(
        encabezado("Fecha"));

tablaEvolucion.addCell(
        encabezado("Presentes"));

tablaEvolucion.addCell(
        encabezado("Sin egreso"));

tablaEvolucion.addCell(
        encabezado("Sin firma"));

// Datos
if (evolucion.isEmpty()) {

    PdfPCell celdaSinDatos =
            new PdfPCell(
                    new Phrase(
                            "No existen datos históricos disponibles.",
                            normal));

    celdaSinDatos.setColspan(4);
    celdaSinDatos.setPadding(8);

    tablaEvolucion.addCell(celdaSinDatos);

} else {

    for (EvolucionAsistenciaDTO e : evolucion) {

        tablaEvolucion.addCell(
                new Phrase(
                        e.getFecha().toString(),
                        normal));

        tablaEvolucion.addCell(
                new Phrase(
                        String.valueOf(
                                e.getPresentes()),
                        normal));

        tablaEvolucion.addCell(
                new Phrase(
                        String.valueOf(
                                e.getSinEgreso()),
                        normal));

        tablaEvolucion.addCell(
                new Phrase(
                        String.valueOf(
                                e.getSinFirmaTotal()),
                        normal));
    }
}

// =====================================================
// BLOQUE ÚNICO: TÍTULO + TABLA
// =====================================================

PdfPTable bloqueEvolucion =
        new PdfPTable(1);

bloqueEvolucion.setWidthPercentage(100);

bloqueEvolucion.setKeepTogether(false);

PdfPCell celdaEvolucion =
        new PdfPCell();

celdaEvolucion.setBorder(
        PdfPCell.NO_BORDER);

celdaEvolucion.setPadding(0);

Paragraph tituloEvo =
        new Paragraph(
                "6. EVOLUCIÓN HISTÓRICA DE LA ASISTENCIA",
                tituloEvolucion
        );

tituloEvo.setSpacingAfter(10);

celdaEvolucion.addElement(tituloEvo);

celdaEvolucion.addElement(
        tablaEvolucion);

bloqueEvolucion.addCell(
        celdaEvolucion);

document.add(
        bloqueEvolucion);

// ==========================================
// GRÁFICO DE EVOLUCIÓN HISTÓRICA
// ==========================================

Image grafico = generarGraficoEvolucion(evolucion);

grafico.scaleToFit(500, 250);

grafico.setAlignment(Element.ALIGN_CENTER);

document.add(grafico);

document.add(
        new Paragraph(" ", normal));

// =====================================================
// 7. DETALLE DE ASISTENCIA
// =====================================================

List<Asistencia> asistenciasReporte =
        asistenciaService.obtenerAsistenciasParaReporte(fecha);

// =====================================================
// TABLA DEL DETALLE DE ASISTENCIA
// =====================================================

PdfPTable tablaDetalle =
        new PdfPTable(6);

tablaDetalle.setWidthPercentage(100);

tablaDetalle.setWidths(
        new float[]{3f, 6f, 3f, 2f, 2f, 3f}
);

tablaDetalle.setHeaderRows(1);

// Encabezados
tablaDetalle.addCell(encabezado("DNI"));
tablaDetalle.addCell(encabezado("Docente"));
tablaDetalle.addCell(encabezado("Turno"));
tablaDetalle.addCell(encabezado("Ingreso"));
tablaDetalle.addCell(encabezado("Egreso"));
tablaDetalle.addCell(encabezado("Estado"));

// =====================================================
// REGISTROS
// =====================================================

if (asistenciasReporte.isEmpty()) {

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

    tablaDetalle.addCell(celdaSinDatos);

} else {

    for (Asistencia a : asistenciasReporte) {

        // DNI
        tablaDetalle.addCell(
                new Phrase(
                        String.valueOf(
                                a.getDocente().getDni()
                        ),
                        normal
                )
        );

        // Docente
        tablaDetalle.addCell(
                new Phrase(
                        a.getDocente().getApellido()
                        + ", "
                        + a.getDocente().getNombre(),
                        normal
                )
        );

        // Turno
        tablaDetalle.addCell(
                new Phrase(
                        a.getTurno().getNombre(),
                        normal
                )
        );

        // Ingreso
        tablaDetalle.addCell(
                new Phrase(
                        a.getHoraIngreso() != null
                                ? a.getHoraIngreso().toString()
                                : "-",
                        normal
                )
        );

        // Egreso
        tablaDetalle.addCell(
                new Phrase(
                        a.getHoraEgreso() != null
                                ? a.getHoraEgreso().toString()
                                : "-",
                        normal
                )
        );

        // Estado
        tablaDetalle.addCell(
        new Phrase(
                obtenerTextoEstado(a.getEstado()),
                normal
        )
);
    }
}
// =====================================================
// BLOQUE ÚNICO: TÍTULO + TABLA
// =====================================================

Font tituloDetalle =
        FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                14
        );

PdfPTable bloqueDetalle =
        new PdfPTable(1);

bloqueDetalle.setWidthPercentage(100);

// Permitimos que la tabla se divida entre páginas,
// pero evitamos que el título quede solo al final.
bloqueDetalle.setKeepTogether(false);

PdfPCell celdaDetalle =
        new PdfPCell();

celdaDetalle.setBorder(
        PdfPCell.NO_BORDER
);

celdaDetalle.setPadding(0);

Paragraph tituloDetalleParrafo =
        new Paragraph(
                "7. DETALLE DE ASISTENCIA DEL DÍA",
                tituloDetalle
        );

tituloDetalleParrafo.setSpacingAfter(10);

celdaDetalle.addElement(
        tituloDetalleParrafo
);

celdaDetalle.addElement(
        tablaDetalle
);

bloqueDetalle.addCell(
        celdaDetalle
);

document.add(
        bloqueDetalle
);

document.add(
        new Paragraph(" ", normal)
);

// =====================================================
//8. CIERRE DEL INFORME
// =====================================================

Font tituloCierre =
        FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                14
        );

Font textoCierre =
        FontFactory.getFont(
                FontFactory.HELVETICA,
                10
        );

PdfPTable bloqueCierre =
        new PdfPTable(1);

bloqueCierre.setWidthPercentage(100);

bloqueCierre.setKeepTogether(true);

PdfPCell celdaCierre =
        new PdfPCell();

celdaCierre.setBorder(
        PdfPCell.NO_BORDER);

celdaCierre.setPadding(0);

Paragraph tituloCierreParrafo =
        new Paragraph(
                "8. CIERRE DEL INFORME",
                tituloCierre
        );

tituloCierreParrafo.setSpacingAfter(10);

celdaCierre.addElement(
        tituloCierreParrafo);

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
                textoCierre
        );

textoFinal.setSpacingAfter(12);

celdaCierre.addElement(
        textoFinal);

Paragraph generado =
        new Paragraph(
                "Informe generado automáticamente por el "
                + "Sistema de Gestión de Asistencia.",
                textoCierre
        );

generado.setAlignment(
        Element.ALIGN_CENTER);

celdaCierre.addElement(
        generado);

bloqueCierre.addCell(
        celdaCierre);

document.add(
        bloqueCierre);

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
private String obtenerTextoEstado(EstadoAsistencia estado) {

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
    
    private PdfPCell encabezado(String texto) {

    Font fuente =
            FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD,
                    10);

    PdfPCell celda =
            new PdfPCell(
                    new Phrase(
                            texto,
                            fuente));

    celda.setHorizontalAlignment(
            Element.ALIGN_CENTER);

    celda.setVerticalAlignment(
            Element.ALIGN_MIDDLE);

    celda.setPadding(7);

    return celda;
}
    private Image generarGraficoEvolucion(
        List<EvolucionAsistenciaDTO> evolucion)
        throws Exception {

    int ancho = 700;
    int alto = 320;

    BufferedImage imagen =
            new BufferedImage(
                    ancho,
                    alto,
                    BufferedImage.TYPE_INT_RGB);

    Graphics2D g =
            imagen.createGraphics();

    // Fondo blanco
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, ancho, alto);

    // Título
    g.setColor(Color.DARK_GRAY);

    g.drawString(
            "Evolución de docentes presentes - últimos 30 días",
            30,
            30);

    // Márgenes del gráfico
    int margenIzquierdo = 55;
    int margenDerecho = 25;
    int margenSuperior = 50;
    int margenInferior = 50;

    int anchoGrafico =
            ancho - margenIzquierdo - margenDerecho;

    int altoGrafico =
            alto - margenSuperior - margenInferior;

    // Buscar máximo
    long maxPresentes =
            evolucion.stream()
                    .mapToLong(
                            EvolucionAsistenciaDTO::getPresentes)
                    .max()
                    .orElse(1);

    if (maxPresentes == 0) {
        maxPresentes = 1;
    }

    // Ejes
    g.setColor(Color.LIGHT_GRAY);

    g.drawLine(
            margenIzquierdo,
            margenSuperior,
            margenIzquierdo,
            alto - margenInferior);

    g.drawLine(
            margenIzquierdo,
            alto - margenInferior,
            ancho - margenDerecho,
            alto - margenInferior);

    // Línea de evolución
    g.setColor(
            new Color(13, 110, 253));

    g.setStroke(
            new BasicStroke(2.5f));

    int cantidad =
            evolucion.size();

    if (cantidad > 1) {

        for (int i = 0;
             i < cantidad - 1;
             i++) {

            long valor1 =
                    evolucion.get(i)
                            .getPresentes();

            long valor2 =
                    evolucion.get(i + 1)
                            .getPresentes();

            int x1 =
                    margenIzquierdo
                    + (i * anchoGrafico
                    / (cantidad - 1));

            int x2 =
                    margenIzquierdo
                    + ((i + 1) * anchoGrafico
                    / (cantidad - 1));

            int y1 =
                    alto - margenInferior
                    - (int)(
                            (valor1 * altoGrafico)
                            / (double) maxPresentes);

            int y2 =
                    alto - margenInferior
                    - (int)(
                            (valor2 * altoGrafico)
                            / (double) maxPresentes);

            g.drawLine(
                    x1,
                    y1,
                    x2,
                    y2);
        }
    }

    g.dispose();

    ByteArrayOutputStream baos =
            new ByteArrayOutputStream();

    ImageIO.write(
            imagen,
            "png",
            baos);

    return Image.getInstance(
            baos.toByteArray());
}
}