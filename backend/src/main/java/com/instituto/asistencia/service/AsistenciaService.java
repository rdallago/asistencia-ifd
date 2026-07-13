package com.instituto.asistencia.service;
import com.instituto.asistencia.entity.DocenteTurno;
import java.util.Observable;
import com.instituto.asistencia.entity.Docente;
import com.instituto.asistencia.dto.EvolucionAsistenciaDTO;
import com.instituto.asistencia.dto.ResumenEjecutivoDTO;
import com.instituto.asistencia.entity.Asistencia;
import com.instituto.asistencia.dto.EstadoGeneralJornadaDTO;



import java.util.ArrayList;
import java.util.List;
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
import java.time.LocalDate;

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
// Estado REAL de los registros
// ======================================

long presentes =
        lista.stream()
                .filter(a ->

                        a.getHoraIngreso() != null
                        &&

                        a.getHoraEgreso() != null

                )
                .count();

long sinEgreso =
        lista.stream()
                .filter(a ->

                        a.getHoraIngreso() != null
                        &&

                        a.getHoraEgreso() == null

                )
                .count();

long sinFirmaTotal =
        lista.stream()
                .filter(a ->

                        a.getHoraIngreso() == null

                )
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
dto.setActividadRegistrada(

        presentes
      + sinEgreso
      + sinFirmaTotal

);
    return dto;

}

public EstadoGeneralJornadaDTO obtenerEstadoGeneralJornada(LocalDate fecha) {

    EstadoGeneralJornadaDTO dto =
            new EstadoGeneralJornadaDTO();

    // Reutilizamos las estadísticas existentes
    EstadisticasDTO est =
            obtenerEstadisticas(fecha);

    long actividad =
            est.getActividadRegistrada();

    // ==========================================
    // Sin actividad
    // ==========================================

    if (actividad == 0) {

        dto.setPuntaje(0);
        dto.setEstado("SIN ACTIVIDAD");
        dto.setDescripcion(
                "No existen registros para la fecha seleccionada.");

        return dto;
    }

    // ==========================================
    // Calidad del Registro Diario
    //
    // Se evalúa únicamente sobre los docentes
    // que iniciaron un registro de asistencia.
    // ==========================================

    long registrosIniciados =
            est.getPresentes()
          + est.getSinEgreso();
    
    dto.setRegistrosCompletos(
        est.getPresentes());

    dto.setRegistrosIniciados(
        registrosIniciados);

    int puntaje;

    if (registrosIniciados == 0) {

        puntaje = 0;

    } else {

        puntaje = (int) Math.round(

                (est.getPresentes() * 100.0)

                / registrosIniciados

        );

    }

    dto.setPuntaje(puntaje);

    // ==========================================
    // Clasificación
    // ==========================================

    if (puntaje >= 90) {

        dto.setEstado("EXCELENTE");

        dto.setDescripcion(
                "La calidad del registro diario es excelente. Todos los registros iniciados fueron completados correctamente.");

    } else if (puntaje >= 75) {

        dto.setEstado("BUENO");

        dto.setDescripcion(
                "La mayoría de los registros iniciados fueron completados correctamente.");

    } else if (puntaje >= 60) {

        dto.setEstado("REGULAR");

        dto.setDescripcion(
                "Se detectan varios registros iniciados sin egreso. Se recomienda revisar la jornada.");

    } else {

        dto.setEstado("CRÍTICO");

        dto.setDescripcion(
                "La mayoría de los registros iniciados quedaron incompletos. Se recomienda realizar seguimiento administrativo.");

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
public List<EvolucionAsistenciaDTO> obtenerEvolucion(
        LocalDate desde,
        LocalDate hasta) {

    

    List<Asistencia> asistencias =
            asistenciaRepository.findByFechaBetweenOrderByFecha(
                    desde,
                    hasta);

    List<EvolucionAsistenciaDTO> resultado =
            new ArrayList<>();

    for (LocalDate fecha = desde;
         !fecha.isAfter(hasta);
         fecha = fecha.plusDays(1)) {

        final LocalDate fechaActual = fecha;

        EvolucionAsistenciaDTO dto =
                new EvolucionAsistenciaDTO();

        dto.setFecha(fechaActual);

        long presentes =
                asistencias.stream()
                        .filter(a ->
                                a.getFecha().equals(fechaActual)
                                &&
                                a.getEstado() == EstadoAsistencia.PRESENTE)
                        .count();

        long sinEgreso =
                asistencias.stream()
                        .filter(a ->
                                a.getFecha().equals(fechaActual)
                                &&
                                a.getEstado() == EstadoAsistencia.SIN_EGRESO)
                        .count();

        long sinFirma =
                asistencias.stream()
                        .filter(a ->
                                a.getFecha().equals(fechaActual)
                                &&
                                a.getEstado() == EstadoAsistencia.SIN_FIRMA_TOTAL)
                        .count();

        dto.setPresentes(presentes);

        dto.setSinEgreso(sinEgreso);

        dto.setSinFirmaTotal(sinFirma);

        resultado.add(dto);

    }

    return resultado;

}
public ResumenEjecutivoDTO obtenerResumenEjecutivo(LocalDate fecha) {

    ResumenEjecutivoDTO dto = new ResumenEjecutivoDTO();

    List<String> conclusiones = new ArrayList<>();

    // ==========================================
    // Estadísticas del día seleccionado
    // ==========================================

    EstadisticasDTO est = obtenerEstadisticas(fecha);

    long actividad = est.getActividadRegistrada();

    if (actividad == 0) {

        conclusiones.add(
                "No existe actividad registrada para la fecha seleccionada.");

        dto.setConclusiones(conclusiones);

        return dto;
    }

    // ==========================================
    // Evolución histórica (últimos 30 días)
    // ==========================================

    LocalDate desde = fecha.minusDays(29);

    List<EvolucionAsistenciaDTO> evolucion =
            obtenerEvolucion(desde, fecha);

    double promedioPresentes =
            evolucion.stream()
                    .mapToLong(EvolucionAsistenciaDTO::getPresentes)
                    .average()
                    .orElse(0);

    double promedioSinEgreso =
            evolucion.stream()
                    .mapToLong(EvolucionAsistenciaDTO::getSinEgreso)
                    .average()
                    .orElse(0);

    double promedioSinFirma =
            evolucion.stream()
                    .mapToLong(EvolucionAsistenciaDTO::getSinFirmaTotal)
                    .average()
                    .orElse(0);

    // ==========================================
    // Indicadores porcentuales
    // ==========================================

    double completos =
            (est.getPresentes() * 100.0) / actividad;

    double pendientes =
            (est.getSinEgreso() * 100.0) / actividad;

    double sinFirma =
            (est.getSinFirmaTotal() * 100.0) / actividad;

    // ==========================================
// Tendencias
// ==========================================

if (est.getPresentes() > promedioPresentes + 0.5) {

    dto.getTendencias().add(
            "📈 La asistencia fue superior al promedio de los últimos 30 días.");

} else if (est.getPresentes() < promedioPresentes - 0.5) {

    dto.getTendencias().add(
            "📉 La asistencia fue inferior al promedio de los últimos 30 días.");

} else {

    dto.getTendencias().add(
            "➖ La asistencia se mantuvo dentro del comportamiento habitual.");
}

// ==========================================
// Observaciones
// ==========================================

if (est.getSinEgreso() > promedioSinEgreso + 0.5) {

    dto.getObservaciones().add(
            "⚠ La cantidad de pendientes de egreso supera el comportamiento habitual.");

} else if (est.getSinEgreso() == 0) {

    dto.getObservaciones().add(
            "✔ No existen pendientes de egreso.");
}    


    // ==========================================
// Observaciones
// ==========================================

if (est.getSinFirmaTotal() > promedioSinFirma + 0.5) {

    dto.getObservaciones().add(
            "🔴 La cantidad de registros sin firma supera el comportamiento habitual.");

} else if (est.getSinFirmaTotal() == 0) {

    dto.getObservaciones().add(
            "✔ No existen registros sin firma.");
}

    // ==========================================
    // Análisis porcentual de la jornada
    // ==========================================

    if (completos >= 90) {

        conclusiones.add(
                "La jornada presenta un excelente nivel de registros completos.");

    } else if (completos >= 70) {

        conclusiones.add(
                "La mayoría de los docentes registró correctamente ingreso y egreso.");

    } else {

        conclusiones.add(
                "Se observa una disminución en los registros completos de la jornada.");
    }

   // ============================================
// Recomendaciones
// ============================================

if (pendientes > 30) {

    dto.getRecomendaciones().add(
            "Revisar el cierre de la jornada y solicitar el registro de egresos pendientes.");

} else if (pendientes > 0) {

    dto.getRecomendaciones().add(
            "Controlar los registros de egreso antes del cierre de la jornada.");

}

   // ============================================
// Recomendaciones
// ============================================

if (sinFirma > 20) {

    dto.getRecomendaciones().add(
            "Verificar los docentes que aún no registraron asistencia y realizar el seguimiento correspondiente.");

}

   // ==========================================
// Estado general de la jornada
// ==========================================

if (est.getSinFirmaTotal() == 0
        && est.getSinEgreso() == 0
        && est.getPresentes() >= promedioPresentes) {

    dto.setEstadoGeneral(
            "🟢 Jornada con funcionamiento excelente.");

} else if (est.getSinFirmaTotal() <= 1
        && est.getSinEgreso() <= 1) {

    dto.setEstadoGeneral(
            "🟡 Jornada con observaciones menores.");

} else {

    dto.setEstadoGeneral(
            "🔴 Jornada que requiere seguimiento administrativo.");

}

    dto.setConclusiones(conclusiones);

    return dto;
}


}




