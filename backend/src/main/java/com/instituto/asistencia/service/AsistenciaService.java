package com.instituto.asistencia.service;
import com.instituto.asistencia.entity.DocenteTurno;
import com.instituto.asistencia.dto.ComparacionHistoricaDTO;
import com.instituto.asistencia.entity.Docente;
import com.instituto.asistencia.dto.EvolucionAsistenciaDTO;
import com.instituto.asistencia.dto.ResumenEjecutivoDTO;
import com.instituto.asistencia.entity.Asistencia;
import org.springframework.transaction.annotation.Transactional;
import com.instituto.asistencia.dto.EstadoGeneralJornadaDTO;
import com.instituto.asistencia.dto.EstadisticasTurnoDTO;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.instituto.asistencia.dto.RankingAusenciasDTO;


import java.util.ArrayList;
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
    private final AuditoriaRespaldoService auditoriaRespaldoService;
    public AsistenciaService(
            AsistenciaRepository asistenciaRepository,
            DocenteRepository docenteRepository,
            TurnoRepository turnoRepository,
            JornadaRepository jornadaRepository,DocenteTurnoRepository docenteTurnoRepository,
            AuditoriaRespaldoService auditoriaRespaldoService) {

        this.asistenciaRepository = asistenciaRepository;
        this.docenteRepository = docenteRepository;
        this.turnoRepository = turnoRepository;
        this.jornadaRepository = jornadaRepository;
        this.docenteTurnoRepository = docenteTurnoRepository;
        this.auditoriaRespaldoService=auditoriaRespaldoService;
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
    // ======================================
// Verificar asignación al turno
// ======================================

boolean asignado =
        docenteTurnoRepository
                .existsByDocente_IdAndTurno_Id(
                        docente.getId(),
                        turno.getId());

if (!asignado) {

    throw new RuntimeException(
            "El docente no está asignado al turno "
                    + turno.getNombre());


}
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

    Authentication authentication =
            SecurityContextHolder
                    .getContext()
                    .getAuthentication();

    String usuario = authentication != null
            ? authentication.getName()
            : "SISTEMA";

    auditoriaRespaldoService.registrar(
            usuario,
            "ASISTENCIAS",
            "INGRESO",
            "RECHAZADO",
            null,
            "Intento de ingreso duplicado para "
                    + docente.getApellido()
                    + ", "
                    + docente.getNombre()
                    + " - DNI: "
                    + docente.getDni()
                    + " - Turno "
                    + turno.getNombre()
    );

    throw new RuntimeException(
            "La entrada ya fue registrada");
}

    asistencia.setHoraIngreso(
            LocalTime.now());

    asistencia.setEstado(
            EstadoAsistencia.PRESENTE);

    asistencia.setFechaCreacion(
            LocalDateTime.now());

    Asistencia guardada =
        asistenciaRepository.save(asistencia);
    Authentication authentication =
        SecurityContextHolder
                .getContext()
                .getAuthentication();

String usuario = authentication.getName();

auditoriaRespaldoService.registrar(
        usuario,
        "ASISTENCIAS",
        "INGRESO",
        "EXITOSO",
        null,
        "Ingreso registrado para "
                + guardada.getDocente().getApellido()
                + ", "
                + guardada.getDocente().getNombre()
                + " - Turno "
                + guardada.getTurno().getNombre()
);

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
    
    boolean asignado =
        docenteTurnoRepository
                .existsByDocente_IdAndTurno_Id(
                        docente.getId(),
                        turno.getId());

if (!asignado) {

    throw new RuntimeException(
            "El docente no está asignado al turno "
                    + turno.getNombre());


}

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

    Authentication authentication =
            SecurityContextHolder
                    .getContext()
                    .getAuthentication();

    String usuario = authentication != null
            ? authentication.getName()
            : "SISTEMA";

    auditoriaRespaldoService.registrar(
            usuario,
            "ASISTENCIAS",
            "EGRESO",
            "RECHAZADO",
            null,
            "Intento de egreso duplicado para "
                    + docente.getApellido()
                    + ", "
                    + docente.getNombre()
                    + " - DNI: "
                    + docente.getDni()
                    + " - Turno "
                    + turno.getNombre()
    );

    throw new RuntimeException(
            "El egreso ya fue registrado");
}

    asistencia.setHoraEgreso(
            LocalTime.now());

    asistencia.setEstado(
            EstadoAsistencia.PRESENTE);

    asistencia.setFechaModificacion(
            LocalDateTime.now());

    Asistencia guardada =
        asistenciaRepository.save(asistencia);
    Authentication authentication =
        SecurityContextHolder
                .getContext()
                .getAuthentication();

String usuario = authentication.getName();

auditoriaRespaldoService.registrar(
        usuario,
        "ASISTENCIAS",
        "EGRESO",
        "EXITOSO",
        null,
        "Egreso registrado para "
                + guardada.getDocente().getApellido()
                + ", "
                + guardada.getDocente().getNombre()
                + " - Turno "
                + guardada.getTurno().getNombre()
);

    return "Salida registrada correctamente";
}
   

    // =====================================================
    // CIERRE DE JORNADA
    // =====================================================
   @Transactional
   public String procesarCierreJornada(Jornada jornada) {

    LocalDate fecha = jornada.getFecha();

    Turno turno = jornada.getTurno();

    List<DocenteTurno> asignados =
        docenteTurnoRepository.findVigentesPorTurnoYFecha(
                turno.getId(),
                fecha);

    for (DocenteTurno dt : asignados) {

        Docente docente = dt.getDocente();

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
            asistencia.setFecha(fecha);

            asistencia.setEstado(
                    EstadoAsistencia.SIN_FIRMA_TOTAL);

            asistencia.setFechaCreacion(
                    LocalDateTime.now());

            Asistencia guardada =
                    asistenciaRepository.save(asistencia);

            auditoriaRespaldoService.registrarEnTransaccion(
                    "SISTEMA",
                    "ASISTENCIAS",
                    "SIN_FIRMA_TOTAL",
                    "EXITOSO",
                    null,
                    "Se registró SIN FIRMA TOTAL para "
                            + guardada.getDocente().getApellido()
                            + ", "
                            + guardada.getDocente().getNombre()
                            + " - Turno "
                            + guardada.getTurno().getNombre()
            );

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

            Asistencia guardada =
                    asistenciaRepository.save(asistencia);

            auditoriaRespaldoService.registrarEnTransaccion(
                    "SISTEMA",
                    "ASISTENCIAS",
                    "SIN_EGRESO",
                    "EXITOSO",
                    null,
                    "Se cerró automáticamente la asistencia de "
                            + guardada.getDocente().getApellido()
                            + ", "
                            + guardada.getDocente().getNombre()
                            + " por falta de egreso - Turno "
                            + guardada.getTurno().getNombre()
            );

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

            asistenciaRepository.save(asistencia);
        }
    }

    return "Asistencia procesada correctamente";
}
@Transactional
public String procesarCierreJornada(Long turnoId) {

    LocalDate hoy = LocalDate.now();

    Jornada jornada = jornadaRepository
            .findByFechaAndTurno_Id(hoy, turnoId)
            .orElseThrow(() ->
                    new RuntimeException(
                            "No existe jornada habilitada"));

    return procesarCierreJornada(jornada);
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

public EstadisticasTurnoDTO obtenerEstadisticasPorTurno(
        LocalDate fecha,
        Long turnoId) {

    EstadisticasTurnoDTO dto =
            new EstadisticasTurnoDTO();

    Turno turno =
            turnoRepository.findById(turnoId)
                    .orElseThrow(() ->
                            new RuntimeException("Turno no encontrado"));

    dto.setTurno(turno.getNombre());

    //=========================================
    // Docentes asignados al turno
    //=========================================

List<DocenteTurno> asignados =
        docenteTurnoRepository.findActivosPorTurnoYFecha(
                turnoId,
                fecha);

System.out.println("Asignados encontrados: " + asignados.size());

for (DocenteTurno dt : asignados) {

    System.out.println(
        dt.getDocente().getApellido()
        + " "
        + dt.getDocente().getNombre()
        + " - Desde: "
        + dt.getFechaDesde()
        + " - Hasta: "
        + dt.getFechaHasta()
    );

}

    dto.setDocentesAsignados(
            asignados.size());
    
    System.out.println(
    "Después del set = " +
    dto.getDocentesAsignados()
);

    //=========================================
    // Asistencias del turno
    //=========================================

    List<Asistencia> asistencias =
            asistenciaRepository
                    .findByTurnoIdAndFecha(
                            turnoId,
                            fecha);
//=========================================
// Conteo de estados
//=========================================

long presentes =
        asistencias.stream()
                .filter(a ->
                        a.getHoraIngreso() != null &&
                        a.getHoraEgreso() != null)
                .count();

long sinEgreso =
        asistencias.stream()
                .filter(a ->
                        a.getHoraIngreso() != null &&
                        a.getHoraEgreso() == null)
                .count();

long sinFirma =
        dto.getDocentesAsignados()
        - presentes
        - sinEgreso;

if (sinFirma < 0) {
    sinFirma = 0;
}

dto.setPresentes(presentes);
dto.setSinEgreso(sinEgreso);
dto.setSinFirmaTotal(sinFirma);

//=========================================
// Porcentaje de asistencia
//=========================================

if (dto.getDocentesAsignados() == 0) {

    dto.setPorcentajeAsistencia(0);

} else {

    dto.setPorcentajeAsistencia(

            (presentes * 100.0)

                    / dto.getDocentesAsignados());

}
System.out.println("====================================");
System.out.println("DTO FINAL");
System.out.println("Turno: " + dto.getTurno());
System.out.println("Asignados: " + dto.getDocentesAsignados());
System.out.println("Presentes: " + dto.getPresentes());
System.out.println("Sin egreso: " + dto.getSinEgreso());
System.out.println("Sin firma: " + dto.getSinFirmaTotal());
System.out.println("%: " + dto.getPorcentajeAsistencia());
System.out.println("====================================");
return dto;
}



public EstadisticasDTO obtenerEstadisticas(LocalDate fecha) {

    EstadisticasDTO dto = new EstadisticasDTO();

    // ======================================
    // Jornadas de la fecha
    // ======================================

    List<Jornada> jornadas =
        jornadaRepository.findByFecha(fecha)
                .stream()
                .filter(jornada ->
                        Boolean.TRUE.equals(jornada.getCerrada()))
                .toList();

    // ======================================
   
    // ======================================
    // Asistencias de jornadas cerradas
    // ======================================

    List<Asistencia> lista =
            obtenerAsistenciasParaReporte(fecha);

    long docentesAsignados = 0;
    long presentes = 0;
    long sinEgreso = 0;
    long sinFirmaTotal = 0;

    for (Jornada jornada : jornadas) {

       List<DocenteTurno> asignadosTurno =
        docenteTurnoRepository.findVigentesPorTurnoYFecha(
                jornada.getTurno().getId(),
                jornada.getFecha()
        );

        for (DocenteTurno asignacion : asignadosTurno) {

            docentesAsignados++;

            Long docenteId =
                    asignacion.getDocente().getId();

            Long turnoId =
                    jornada.getTurno().getId();

            Long jornadaId =
                    jornada.getId();

            Asistencia asistenciaEncontrada = null;

            for (Asistencia asistencia : lista) {

                if (asistencia.getDocente() != null
                        && asistencia.getTurno() != null
                        && asistencia.getJornada() != null
                        && docenteId.equals(
                                asistencia.getDocente().getId())
                        && turnoId.equals(
                                asistencia.getTurno().getId())
                        && jornadaId.equals(
                                asistencia.getJornada().getId())) {

                    asistenciaEncontrada = asistencia;
                    break;
                }
            }
            

            if (asistenciaEncontrada == null
                    || asistenciaEncontrada.getHoraIngreso() == null) {

                sinFirmaTotal++;

            } else if (asistenciaEncontrada.getHoraEgreso() == null) {

                sinEgreso++;

            } else {

                presentes++;
            }
        }
    }

    // ======================================
    // Estadísticas de asistencia
    // ======================================

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
    // Porcentaje de asistencia
    // ======================================

    if (docentesAsignados == 0) {

        dto.setPorcentajeAsistencia(0.0);

    } else {

        dto.setPorcentajeAsistencia(
                (presentes * 100.0)
                        / docentesAsignados
        );
    }

    // ======================================
    // Actividad registrada
    // ======================================

    dto.setActividadRegistrada(
            presentes
                    + sinEgreso
                    + sinFirmaTotal
    );

    System.out.println("=================================");
    System.out.println("SALGO DE OBTENER ESTADISTICAS");
    System.out.println("Fecha: " + fecha);
    System.out.println("Jornadas encontradas: " + jornadas.size());
    System.out.println("Docentes asignados: " + docentesAsignados);
    System.out.println("Presentes: " + presentes);
    System.out.println("Sin egreso: " + sinEgreso);
    System.out.println("Sin firma total: " + sinFirmaTotal);
    System.out.println("Porcentaje: " + dto.getPorcentajeAsistencia());
    System.out.println("Actividad registrada: "
            + dto.getActividadRegistrada());
    System.out.println("=================================");

    return dto;
}


public EstadoGeneralJornadaDTO obtenerEstadoGeneralJornada(
        LocalDate fecha) {

    EstadoGeneralJornadaDTO dto =
            new EstadoGeneralJornadaDTO();

    // ==========================================
    // Verificar jornadas cerradas
    // ==========================================

    boolean tieneJornadaCerrada =
            jornadaRepository.findByFecha(fecha)
                    .stream()
                    .anyMatch(j ->
                            Boolean.TRUE.equals(j.getCerrada()));

    if (!tieneJornadaCerrada) {

        dto.setPuntaje(0);
        dto.setRegistrosCompletos(0);
        dto.setRegistrosIniciados(0);
        dto.setEstado("SIN DATOS DEFINITIVOS");
        dto.setDescripcion(
                "No existen jornadas cerradas para la fecha seleccionada.");

        return dto;
    }

    // ==========================================
    // Estadísticas de jornadas cerradas
    // ==========================================

    EstadisticasDTO est =
            obtenerEstadisticas(fecha);

    long registrosIniciados =
            est.getPresentes() + est.getSinEgreso();

    long registrosCompletos =
            est.getPresentes();

    dto.setRegistrosCompletos(registrosCompletos);
    dto.setRegistrosIniciados(registrosIniciados);

    // ==========================================
    // Sin registros iniciados
    // ==========================================

    if (registrosIniciados == 0) {

        dto.setPuntaje(0);
        dto.setEstado("SIN REGISTROS INICIADOS");

        if (est.getSinFirmaTotal() > 0) {

            dto.setDescripcion(
                    "Existen docentes asignados sin firma de asistencia. "
                    + "No es posible evaluar la calidad de finalización "
                    + "porque ningún docente inició su registro.");

        } else {

            dto.setDescripcion(
                    "La jornada está cerrada, pero no existen "
                    + "asignaciones docentes para evaluar.");
        }

        return dto;
    }

    // ==========================================
    // Calidad de registros iniciados
    // ==========================================

    int puntaje = (int) Math.round(
            registrosCompletos * 100.0
            / registrosIniciados
    );

    dto.setPuntaje(puntaje);

    // ==========================================
    // Clasificación
    // ==========================================

    if (puntaje == 100) {

        dto.setEstado("EXCELENTE");
        dto.setDescripcion(
                "Todos los registros de asistencia iniciados "
                + "fueron completados con ingreso y egreso.");

    } else if (puntaje >= 90) {

        dto.setEstado("EXCELENTE");
        dto.setDescripcion(
                "La calidad de finalización de los registros "
                + "iniciados es excelente, aunque existen "
                + "algunos registros pendientes de egreso.");

    } else if (puntaje >= 75) {

        dto.setEstado("BUENO");
        dto.setDescripcion(
                "La mayoría de los registros iniciados "
                + "fueron completados correctamente.");

    } else if (puntaje >= 60) {

        dto.setEstado("REGULAR");
        dto.setDescripcion(
                "Se identificaron registros iniciados "
                + "que quedaron sin egreso.");

    } else {

        dto.setEstado("CRÍTICO");
        dto.setDescripcion(
                "Una proporción importante de los registros "
                + "iniciados quedó sin egreso. "
                + "Se recomienda seguimiento administrativo.");
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

    List<EvolucionAsistenciaDTO> resultado =
            new ArrayList<>();

    List<Jornada> jornadasCerradas =
            jornadaRepository.findAll()
                    .stream()
                    .filter(j ->
                            Boolean.TRUE.equals(j.getCerrada())
                            && !j.getFecha().isBefore(desde)
                            && !j.getFecha().isAfter(hasta))
                    .toList();

    List<LocalDate> fechasCerradas =
            jornadasCerradas.stream()
                    .map(Jornada::getFecha)
                    .distinct()
                    .sorted()
                    .toList();

    for (LocalDate fecha : fechasCerradas) {

        EstadisticasDTO estadisticas =
                obtenerEstadisticas(fecha);

        EvolucionAsistenciaDTO dto =
                new EvolucionAsistenciaDTO();

        dto.setFecha(fecha);
        dto.setPresentes(estadisticas.getPresentes());
        dto.setSinEgreso(estadisticas.getSinEgreso());
        dto.setSinFirmaTotal(estadisticas.getSinFirmaTotal());

        resultado.add(dto);
    }

    return resultado;
}


public ResumenEjecutivoDTO obtenerResumenEjecutivo(LocalDate fecha) {

    ResumenEjecutivoDTO dto = new ResumenEjecutivoDTO();
    List<String> conclusiones = new ArrayList<>();

    // ==========================================
    // Verificar jornadas cerradas
    // ==========================================

    boolean tieneJornadaCerrada =
            jornadaRepository.findByFecha(fecha)
                    .stream()
                    .anyMatch(j ->
                            Boolean.TRUE.equals(j.getCerrada()));

    if (!tieneJornadaCerrada) {

        dto.setEstadoGeneral(
                "Sin jornadas cerradas para la fecha seleccionada.");

        dto.getTendencias().add(
                "No es posible establecer una tendencia con datos no definitivos.");

        dto.getObservaciones().add(
                "La fecha no presenta jornadas cerradas.");

        dto.getRecomendaciones().add(
                "Consultar nuevamente cuando se hayan cerrado las jornadas.");

        conclusiones.add(
                "No existen datos definitivos de asistencia para esta fecha.");

        dto.setConclusiones(conclusiones);
        return dto;
    }

    // ==========================================
    // Estadísticas del día
    // ==========================================

    EstadisticasDTO est = obtenerEstadisticas(fecha);

    long total = est.getActividadRegistrada();

    if (total == 0) {

        dto.setEstadoGeneral(
                "Jornada cerrada sin docentes asignados.");

        dto.getObservaciones().add(
                "No se identificaron asignaciones docentes vigentes para la jornada.");

        conclusiones.add(
                "No es posible calcular indicadores de asistencia sin docentes asignados.");

        dto.setConclusiones(conclusiones);
        return dto;
    }

    double porcentajeAsistencia =
            est.getPorcentajeAsistencia();

    double porcentajeSinEgreso =
            est.getSinEgreso() * 100.0 / total;

    double porcentajeSinFirma =
            est.getSinFirmaTotal() * 100.0 / total;

    // ==========================================
    // Comparación histórica
    // ==========================================

    ComparacionHistoricaDTO comparacion =
            obtenerComparacionHistorica(fecha);

    String tendencia = comparacion.getTendencia();

    if ("SUBE".equals(tendencia)) {

        dto.getTendencias().add(
                "La asistencia institucional supera el promedio histórico en "
                + comparacion.getDiferencia()
                + " puntos porcentuales.");

    } else if ("BAJA".equals(tendencia)) {

        dto.getTendencias().add(
                "La asistencia institucional se encuentra por debajo del promedio histórico en "
                + Math.abs(comparacion.getDiferencia())
                + " puntos porcentuales.");

    } else if ("ESTABLE".equals(tendencia)) {

        dto.getTendencias().add(
                "La asistencia institucional se mantiene próxima al promedio histórico.");

    } else {

        dto.getTendencias().add(
                "No existen datos suficientes para establecer una tendencia.");
    }

    // ==========================================
    // Observaciones
    // ==========================================

    if (est.getSinEgreso() > 0) {

        dto.getObservaciones().add(
                "Se identificaron "
                + est.getSinEgreso()
                + " registros con ingreso, pero sin egreso.");

    } else {

        dto.getObservaciones().add(
                "No se identificaron registros pendientes de egreso.");
    }

    if (est.getSinFirmaTotal() > 0) {

        dto.getObservaciones().add(
                "Se identificaron "
                + est.getSinFirmaTotal()
                + " asignaciones docentes sin firma de asistencia.");

    } else {

        dto.getObservaciones().add(
                "No se identificaron asignaciones sin firma de asistencia.");
    }

    // ==========================================
    // Conclusiones
    // ==========================================

    conclusiones.add(
            String.format(
                    java.util.Locale.forLanguageTag("es-AR"),
                    "La asistencia institucional de las jornadas cerradas fue del %.1f%%.",
                    porcentajeAsistencia));

    conclusiones.add(
            "Se registraron "
            + est.getPresentes()
            + " asistencias completas sobre "
            + total
            + " asignaciones docentes consideradas.");

    if (porcentajeSinFirma > 0) {

        conclusiones.add(
                "La principal situación a verificar es la existencia de docentes sin firma de asistencia.");
    }

    // ==========================================
    // Recomendaciones
    // ==========================================

    if (porcentajeSinEgreso > 0) {

        dto.getRecomendaciones().add(
                "Revisar administrativamente los registros que quedaron sin egreso.");
    }

    if (porcentajeSinFirma > 0) {

        dto.getRecomendaciones().add(
                "Realizar el seguimiento de las asignaciones docentes sin firma.");
    }

    if (porcentajeSinEgreso == 0
            && porcentajeSinFirma == 0) {

        dto.getRecomendaciones().add(
                "Mantener los procedimientos de registro y control de asistencia.");
    }

    // ==========================================
    // Estado general
    // ==========================================

    if (porcentajeAsistencia >= 90) {

        dto.setEstadoGeneral(
                "Jornada cerrada con alto nivel de asistencia completa.");

    } else if (porcentajeAsistencia >= 70) {

        dto.setEstadoGeneral(
                "Jornada cerrada con situaciones de asistencia a revisar.");

    } else {

        dto.setEstadoGeneral(
                "Jornada cerrada que requiere seguimiento administrativo.");
    }

    dto.setConclusiones(conclusiones);

    return dto;
}



public ComparacionHistoricaDTO obtenerComparacionHistorica(
        LocalDate fecha) {

    ComparacionHistoricaDTO dto =
            new ComparacionHistoricaDTO();

    // ==========================================
    // Período histórico:
    // 29 días anteriores a la fecha consultada
    // ==========================================

    LocalDate desde = fecha.minusDays(29);
    LocalDate hasta = fecha.minusDays(1);

    List<EvolucionAsistenciaDTO> evolucion =
            obtenerEvolucion(desde, hasta);

    // ==========================================
    // Excluir jornadas sin asignaciones
    // ==========================================

    List<EvolucionAsistenciaDTO> diasValidos =
            evolucion.stream()
                    .filter(e ->
                            e.getPresentes()
                            + e.getSinEgreso()
                            + e.getSinFirmaTotal() > 0)
                    .toList();

    // ==========================================
    // Promedio histórico
    // ==========================================

    double promedio = diasValidos.stream()
            .mapToDouble(e -> {

                long total =
                        e.getPresentes()
                        + e.getSinEgreso()
                        + e.getSinFirmaTotal();

                return e.getPresentes()
                        * 100.0 / total;

            })
            .average()
            .orElse(0);

    dto.setPromedioHistorico(
            Math.round(promedio * 10.0) / 10.0
    );

    // ==========================================
    // Comprobar jornada cerrada en fecha actual
    // ==========================================

    boolean tieneJornadaCerrada =
            jornadaRepository.findByFecha(fecha)
                    .stream()
                    .anyMatch(j ->
                            Boolean.TRUE.equals(j.getCerrada()));

    if (!tieneJornadaCerrada) {

        dto.setValorActual(0);
        dto.setDiferencia(0);
        dto.setTendencia("SIN DATOS");

        return dto;
    }

    // ==========================================
    // Estadísticas actuales
    // ==========================================

    EstadisticasDTO est =
            obtenerEstadisticas(fecha);

    if (est.getActividadRegistrada() == 0) {

        dto.setValorActual(0);
        dto.setDiferencia(0);
        dto.setTendencia("SIN DATOS");

        return dto;
    }

    double actual =
            est.getPorcentajeAsistencia();

    dto.setValorActual(
            Math.round(actual * 10.0) / 10.0
    );

    // ==========================================
    // Sin antecedentes históricos
    // ==========================================

    if (diasValidos.isEmpty()) {

        dto.setDiferencia(0);
        dto.setTendencia("SIN DATOS HISTÓRICOS");

        return dto;
    }

    // ==========================================
    // Comparación real
    // ==========================================

    double diferencia = actual - promedio;

    dto.setDiferencia(
            Math.round(diferencia * 10.0) / 10.0
    );

    if (diferencia > 3) {

        dto.setTendencia("SUBE");

    } else if (diferencia < -3) {

        dto.setTendencia("BAJA");

    } else {

        dto.setTendencia("ESTABLE");
    }

    return dto;
}

public List<Asistencia> obtenerAsistenciasParaReporte(LocalDate fecha) {

    List<Jornada> jornadasCerradas =
            jornadaRepository.findByFecha(fecha)
                    .stream()
                    .filter(jornada ->
                            Boolean.TRUE.equals(jornada.getCerrada()))
                    .toList();

    List<Long> idsJornadasCerradas =
            jornadasCerradas.stream()
                    .map(Jornada::getId)
                    .toList();

    return asistenciaRepository.findByFecha(fecha)
            .stream()
            .filter(asistencia ->
                    asistencia.getJornada() != null
                    && idsJornadasCerradas.contains(
                            asistencia.getJornada().getId()))
            .toList();
}
public List<RankingAusenciasDTO> obtenerRankingAusencias(
        LocalDate desde,
        LocalDate hasta) {

    if (desde == null || hasta == null) {
        throw new IllegalArgumentException(
                "Las fechas desde y hasta son obligatorias");
    }

    if (desde.isAfter(hasta)) {
        throw new IllegalArgumentException(
                "La fecha desde no puede ser posterior a la fecha hasta");
    }

    return asistenciaRepository.obtenerRankingAusencias(
            desde,
            hasta);
}

}