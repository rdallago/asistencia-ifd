package com.instituto.asistencia.service;
import com.instituto.asistencia.entity.DocenteTurno;
import com.instituto.asistencia.entity.Asistencia;
import com.instituto.asistencia.entity.Docente;
import com.instituto.asistencia.dto.EstadisticasDTO;
import java.time.LocalDate;
import com.instituto.asistencia.entity.Jornada;
import com.itextpdf.text.BaseColor;
import com.instituto.asistencia.dto.EstadisticasDTO;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.Rectangle;
import java.time.format.DateTimeFormatter;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.instituto.asistencia.entity.Turno;
import com.itextpdf.text.Image;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Element;
import com.instituto.asistencia.dto.ReporteAsistenciaDTO;
import com.instituto.asistencia.entity.enums.EstadoAsistencia;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import com.instituto.asistencia.repository.AsistenciaRepository;
import com.instituto.asistencia.repository.DocenteRepository;
import com.instituto.asistencia.repository.JornadaRepository;
import com.instituto.asistencia.repository.TurnoRepository;
import com.instituto.asistencia.repository.DocenteTurnoRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import com.instituto.asistencia.dto.IndicadoresInstitucionalesDTO;

@Service
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final DocenteRepository docenteRepository;
    private final TurnoRepository turnoRepository;
    private final JornadaRepository jornadaRepository;
    private final DocenteTurnoRepository docenteTurnoRepository;
    public AsistenciaService(
            AsistenciaRepository asistenciaRepository,
            DocenteRepository docenteRepository,
            TurnoRepository turnoRepository,
            JornadaRepository jornadaRepository,DocenteTurnoRepository docenteTurnoRepository) {

        this.asistenciaRepository = asistenciaRepository;
        this.docenteRepository = docenteRepository;
        this.turnoRepository = turnoRepository;
        this.jornadaRepository = jornadaRepository;
        this.docenteTurnoRepository = docenteTurnoRepository;
    }

    // =====================================================
    // MARCAR INGRESO
    // =====================================================

   public String marcarEntrada(
        String dni,
        Long jornadaId) {

    LocalDate hoy = LocalDate.now();
    LocalTime ahora = LocalTime.now();

    Jornada jornada =
            jornadaRepository.findById(jornadaId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Jornada no encontrada"));

    if (!jornada.getHabilitada()) {

        throw new RuntimeException(
                "La jornada no está habilitada");
    }

    if (jornada.getCerrada()) {

        throw new RuntimeException(
                "La jornada ya fue cerrada");
    }

    Turno turno = jornada.getTurno();

    Docente docente =
            docenteRepository.findByDni(dni)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Docente no encontrado"));

    if (ahora.isBefore(turno.getHoraInicio())) {

        throw new RuntimeException(
                "Aún no comenzó el turno");
    }

    Asistencia asistencia =
            asistenciaRepository
                    .findByDocenteIdAndTurnoIdAndJornadaId(
                            docente.getId(),
                            turno.getId(),
                            jornada.getId())
                    .orElse(new Asistencia());

    asistencia.setDocente(docente);

    asistencia.setTurno(turno);

    asistencia.setJornada(jornada);

    asistencia.setFecha(hoy);

    if (asistencia.getHoraIngreso() != null) {

        throw new RuntimeException(
                "La entrada ya fue registrada");
    }

    asistencia.setHoraIngreso(
            LocalTime.now());

    asistencia.setEstado(
            EstadoAsistencia.PRESENTE);

    asistencia.setFechaCreacion(
            LocalDateTime.now());

    asistenciaRepository.save(asistencia);

    return "Entrada registrada correctamente";
}
    // =====================================================
    // MARCAR EGRESO
    // =====================================================

   public String marcarSalida(
        String dni,
        Long jornadaId) {

    Jornada jornada =
            jornadaRepository.findById(jornadaId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Jornada no encontrada"));

    if (!jornada.getHabilitada()) {

        throw new RuntimeException(
                "La jornada no está habilitada");
    }

    if (jornada.getCerrada()) {

        throw new RuntimeException(
                "La jornada ya fue cerrada");
    }

    Turno turno = jornada.getTurno();

    Docente docente =
            docenteRepository.findByDni(dni)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Docente no encontrado"));

    Asistencia asistencia =
            asistenciaRepository
                    .findByDocenteIdAndTurnoIdAndJornadaId(
                            docente.getId(),
                            turno.getId(),
                            jornada.getId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "No existe ingreso registrado"));

    if (asistencia.getHoraEgreso() != null) {

        throw new RuntimeException(
                "El egreso ya fue registrado");
    }

    asistencia.setHoraEgreso(
            LocalTime.now());

    asistencia.setEstado(
            EstadoAsistencia.PRESENTE);

    asistencia.setFechaModificacion(
            LocalDateTime.now());

    asistenciaRepository.save(asistencia);

    return "Salida registrada correctamente";
}
   

    // =====================================================
    // CIERRE DE JORNADA
    // =====================================================

public String procesarCierreJornada(Long turnoId) {

    LocalDate hoy = LocalDate.now();

    Jornada jornada =
            jornadaRepository
                    .findByFechaAndTurno_Id(
                            hoy,
                            turnoId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "No existe jornada habilitada"));

    Turno turno = jornada.getTurno();

    List<DocenteTurno> asignados =
            docenteTurnoRepository
                    .findByTurno_Id(
                            turno.getId());

    for (DocenteTurno dt : asignados) {

        Docente docente =
                dt.getDocente();

        Asistencia asistencia =
                asistenciaRepository
                     .findByDocenteIdAndTurnoIdAndJornadaId(
    docente.getId(),
    turno.getId(),
    jornada.getId())
                        .orElse(null);

        // =====================================
        // SIN FIRMA TOTAL
        // =====================================

        if (asistencia == null) {

            asistencia = new Asistencia();

            asistencia.setDocente(docente);
            asistencia.setTurno(turno);
            asistencia.setJornada(jornada);

            asistencia.setFecha(hoy);

            asistencia.setEstado(
                    EstadoAsistencia.SIN_FIRMA_TOTAL);

            asistencia.setFechaCreacion(
                    LocalDateTime.now());

            asistenciaRepository.save(
                    asistencia);

            continue;
        }

        // =====================================
        // INGRESO SIN EGRESO
        // =====================================

        if (asistencia.getHoraIngreso() != null
                && asistencia.getHoraEgreso() == null) {

            asistencia.setEstado(
                    EstadoAsistencia.SIN_EGRESO);

            asistencia.setFechaModificacion(
                    LocalDateTime.now());

            asistenciaRepository.save(
                    asistencia);

            continue;
        }

        // =====================================
        // PRESENTE
        // =====================================

        if (asistencia.getHoraIngreso() != null
                && asistencia.getHoraEgreso() != null) {

            asistencia.setEstado(
                    EstadoAsistencia.PRESENTE);

            asistencia.setFechaModificacion(
                    LocalDateTime.now());

            asistenciaRepository.save(
                    asistencia);
        }
    }

    
    return "Asistencia procesada correctamente";
}
// =====================================================
    // REPORTE DIARIO
    // =====================================================

public List<Asistencia> reporteDiario(LocalDate fecha) {
    return asistenciaRepository.findByFecha(fecha);
}

    // =====================================================
    // GENERACION PDF
    // =====================================================
public byte[] generarPdf(LocalDate fecha, Long turnoId, Long docenteId, String estado, String usuario) {
    try {
        String nombreTurno = "Todos";
        if (turnoId != null) {
            nombreTurno = turnoRepository.findById(turnoId)
                    .map(t -> t.getNombre())
                    .orElse("Desconocido");
        }

        String tituloReporte;
        if (estado != null && !estado.isBlank()) {
            switch (estado.toUpperCase()) {
                case "PRESENTE" -> tituloReporte = "REPORTE DE DOCENTES PRESENTES";
                case "SIN_EGRESO" -> tituloReporte = "REPORTE DE DOCENTES SIN EGRESO";
                case "SIN_FIRMA_TOTAL" -> tituloReporte = "REPORTE DE DOCENTES AUSENTES";
                default -> tituloReporte = "REPORTE DE ASISTENCIAS";
            }
        } else if (docenteId != null) {
            tituloReporte = "REPORTE POR DOCENTE";
        } else if (turnoId != null) {
            tituloReporte = "REPORTE DE ASISTENCIAS - " + nombreTurno.toUpperCase();
        } else {
            tituloReporte = "REPORTE GENERAL DE ASISTENCIAS";
        }

        List<ReporteAsistenciaDTO> datos = reporte(fecha, turnoId, docenteId, estado);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(com.itextpdf.text.PageSize.A4, 36, 36, 36, 70);
        PdfWriter.getInstance(document, baos);

        document.open();

        Image logo = Image.getInstance(getClass().getResource("/static/logo-ifd.png"));
        logo.scaleToFit(250, 250);
        logo.setAlignment(Element.ALIGN_CENTER);
        document.add(logo);

        Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph pTitulo = new Paragraph("INSTITUTO SUPERIOR DE FORMACIÓN\nDOCENTE Y TÉCNICA", titulo);
        pTitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(pTitulo);

        document.add(new Paragraph(" "));

        PdfPTable filtros = new PdfPTable(2);
        filtros.setWidthPercentage(50);
        filtros.setHorizontalAlignment(Element.ALIGN_LEFT);
        filtros.addCell(encabezado("Filtro"));
        filtros.addCell(encabezado("Valor"));
        filtros.addCell("Fecha");
        filtros.addCell(fecha.toString());
        filtros.addCell("Turno");
        filtros.addCell(nombreTurno);
        filtros.addCell("Docente");
        filtros.addCell(docenteId == null ? "Todos" : docenteId.toString());
        filtros.addCell("Estado");
        filtros.addCell(estado == null || estado.isBlank() ? "Todos" : estado);

        Font subtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Paragraph reporte = new Paragraph(tituloReporte, subtitulo);
        reporte.setAlignment(Element.ALIGN_CENTER);
        document.add(reporte);
        document.add(new Paragraph(" "));
        document.add(filtros);
        document.add(new Paragraph(" "));

        PdfPTable tabla = new PdfPTable(6);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(10);
        tabla.setWidths(new float[]{3f, 6f, 3f, 2f, 2f, 3f});

        tabla.addCell(encabezado("DNI"));
        tabla.addCell(encabezado("Docente"));
        tabla.addCell(encabezado("Turno"));
        tabla.addCell(encabezado("Ingreso"));
        tabla.addCell(encabezado("Egreso"));
        tabla.addCell(encabezado("Estado"));

        for (ReporteAsistenciaDTO r : datos) {
            tabla.addCell(dato(r.getDni()));
            tabla.addCell(datoIzquierda(r.getDocente()));
            tabla.addCell(dato(r.getTurno()));
            tabla.addCell(dato(r.getIngreso() == null ? "-" : r.getIngreso().toString()));
            tabla.addCell(dato(r.getEgreso() == null ? "-" : r.getEgreso().toString()));

            String estadoTexto = switch (r.getEstado()) {
                case "PRESENTE" -> "Presente";
                case "SIN_EGRESO" -> "Sin egreso";
                case "SIN_FIRMA_TOTAL" -> "Sin firma";
                default -> r.getEstado();
            };
            tabla.addCell(dato(estadoTexto));
        }

        document.add(tabla);
        document.add(new Paragraph("Fecha del reporte: " + fecha));
        document.add(new Paragraph("Cantidad de registros: " + datos.size()));

        document.close();

        byte[] pdfBase = baos.toByteArray();
        return agregarPieConPaginas(pdfBase,usuario);

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
 
    
    
    private Jornada obtenerJornadaActiva(
        Long turnoId) {

    LocalDate hoy = LocalDate.now();

    Jornada jornada =
            jornadaRepository
                    .findByFechaAndTurno_Id(
                            hoy,
                            turnoId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "No existe jornada habilitada"));

    if (!jornada.getHabilitada()) {

        throw new RuntimeException(
                "La jornada no está habilitada");
    }

    if (jornada.getCerrada()) {

        throw new RuntimeException(
                "La jornada ya fue cerrada");
    }

    return jornada;
}
public List<ReporteAsistenciaDTO> reporte(
        LocalDate fecha,
        Long turnoId,
        Long docenteId,
        String estado) {

    List<Asistencia> lista =
            asistenciaRepository.findByFecha(
                    fecha);
    
    if (turnoId != null) {
        
    
        lista = lista.stream()

                .filter(a ->
                        a.getTurno()
                         .getId()
                         .equals(turnoId))

                .toList();
    }

    if (docenteId != null) {

        lista = lista.stream()

                .filter(a ->
                        a.getDocente()
                         .getId()
                         .equals(docenteId))

                .toList();
    }
    if (estado != null &&
    !estado.isBlank()) {

    lista = lista.stream()

            .filter(a ->
                    a.getEstado()
                     .name()
                     .equalsIgnoreCase(
                             estado))

            .toList();
}

    return lista.stream()

            .map(a -> new ReporteAsistenciaDTO(

                    a.getDocente().getDni(),

                    a.getDocente().getApellido()
                            + ", "
                            + a.getDocente().getNombre(),

                    a.getTurno().getNombre(),

                    a.getHoraIngreso(),

                    a.getHoraEgreso(),

                    a.getEstado().name()
            ))

            .toList();
    
}
private PdfPCell encabezado(String texto){

    Font fuente =
            FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD,
                    9);

    PdfPCell celda =
            new PdfPCell(
                    new Phrase(texto,fuente));

    celda.setHorizontalAlignment(
            Element.ALIGN_CENTER);

    celda.setVerticalAlignment(
            Element.ALIGN_MIDDLE);

    celda.setBackgroundColor(
      
    new BaseColor(0,70,140));

celda.setBorderWidth(1.2f);

celda.setPadding(8);

fuente.setColor(BaseColor.WHITE);

    celda.setPadding(8);

    return celda;
}
 private PdfPCell dato(String texto) {

    Font fuenteDato = FontFactory.getFont(
            FontFactory.HELVETICA,
            8);

    PdfPCell c = new PdfPCell(
            new Phrase(
                    texto == null ? "" : texto,
                    fuenteDato));

    c.setPaddingTop(7);
    c.setPaddingBottom(7);
    c.setPaddingLeft(5);
    c.setPaddingRight(5);

    c.setBorderWidth(0.8f);

    c.setHorizontalAlignment(Element.ALIGN_CENTER);

    c.setVerticalAlignment(Element.ALIGN_MIDDLE);

    return c;
}
 private PdfPCell datoIzquierda(String texto) {

    Font fuenteDato = FontFactory.getFont(
            FontFactory.HELVETICA,
            8);

    PdfPCell c = new PdfPCell(
            new Phrase(
                    texto == null ? "" : texto,
                    fuenteDato));

    c.setPaddingTop(7);
    c.setPaddingBottom(7);
    c.setPaddingLeft(5);
    c.setPaddingRight(5);

    c.setBorderWidth(0.5f);

    c.setHorizontalAlignment(Element.ALIGN_LEFT);

    c.setVerticalAlignment(Element.ALIGN_MIDDLE);

    return c;
}
private byte[] agregarPieConPaginas(byte[] pdfBase, String usuario) {
    try {
        PdfReader reader = new PdfReader(pdfBase);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, out);

        int total = reader.getNumberOfPages();
        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
        String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        for (int i = 1; i <= total; i++) {
            PdfContentByte canvas = stamper.getOverContent(i);
            Rectangle pageSize = reader.getPageSize(i);

            float left = pageSize.getLeft(36);
            float right = pageSize.getRight(36);
            float yPagina = pageSize.getBottom(18);
            float yUsuario = pageSize.getBottom(30);
            float yEmitido = pageSize.getBottom(42);
            canvas.beginText();
            canvas.setFontAndSize(bf, 8);

            canvas.showTextAligned(Element.ALIGN_LEFT,
                    "Sistema Integral de Asistencia Docente",
                    left, yPagina, 0);

            canvas.showTextAligned(Element.ALIGN_LEFT,
                    "Instituto Superior de Formación Docente y Técnica Dr. J. A. Ferreira",
                    left, yPagina - 10, 0);

            canvas.showTextAligned(Element.ALIGN_RIGHT,
                    "Emitido: " + fechaHora,
                    right, yEmitido, 0);
            
            canvas.showTextAligned(
        Element.ALIGN_RIGHT,
        "Usuario: " + usuario,
        right,
        yUsuario,
        0);
            
            
      

            canvas.showTextAligned(Element.ALIGN_RIGHT,
                    "Página " + i + " / " + total,
                    right, yPagina, 0);

            canvas.endText();
        }

        stamper.close();
        reader.close();
        return out.toByteArray();
    } catch (Exception e) {
        throw new RuntimeException("Error al agregar pie de página", e);
    }
}
public EstadisticasDTO obtenerEstadisticas(LocalDate fecha) {

    List<Asistencia> lista =
            asistenciaRepository.findByFecha(fecha);

    EstadisticasDTO dto = new EstadisticasDTO();

    // ======================================
    // Asistencia
    // ======================================

    long presentes =
            lista.stream()
                    .filter(a ->
                            a.getEstado() ==
                            EstadoAsistencia.PRESENTE)
                    .count();

    long sinEgreso =
            lista.stream()
                    .filter(a ->
                            a.getEstado() ==
                            EstadoAsistencia.SIN_EGRESO)
                    .count();

    long sinFirmaTotal =
            lista.stream()
                    .filter(a ->
                            a.getEstado() ==
                            EstadoAsistencia.SIN_FIRMA_TOTAL)
                    .count();

    dto.setPresentes(presentes);

    dto.setSinEgreso(sinEgreso);

    dto.setSinFirmaTotal(sinFirmaTotal);

    // ======================================
    // Institucionales
    // ======================================

    dto.setDocentesRegistrados(
            docenteRepository.count());

    dto.setDocentesActivos(
            docenteRepository.countByActivoTrue());

    dto.setTotalDocentes(
            docenteRepository.count());

    dto.setTurnosConfigurados(
            turnoRepository.count());

    dto.setJornadasAbiertas(
            jornadaRepository.countByCerradaFalse());

    dto.setAsignacionesVigentes(
            docenteTurnoRepository.countByActivoTrue());

    // ======================================
    // Porcentaje asistencia
    // ======================================

    if (dto.getDocentesRegistrados() == 0) {

        dto.setPorcentajeAsistencia(0.0);

    } else {

        dto.setPorcentajeAsistencia(

                (presentes * 100.0)

                        / dto.getDocentesRegistrados());

    }

    return dto;

}
public IndicadoresInstitucionalesDTO obtenerIndicadoresInstitucionales() {

    IndicadoresInstitucionalesDTO dto =
            new IndicadoresInstitucionalesDTO();

    dto.setDocentesRegistrados(

            docenteRepository.count());

    dto.setDocentesActivos(

            (long) docenteRepository
                    .findByActivoTrue()
                    .size());

    dto.setTurnosConfigurados(

            turnoRepository.count());

    dto.setAsignacionesVigentes(

            (long) docenteTurnoRepository
                    .findByActivoTrue()
                    .size());

    dto.setJornadasAbiertas(

            (long) jornadaRepository
                    .findByCerradaFalse()
                    .size());

    return dto;

}

}




