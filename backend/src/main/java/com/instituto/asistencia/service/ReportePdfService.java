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


            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            Document document = new Document();

            PdfWriter.getInstance(document, baos);

            document.open();

            Font titulo =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            18);

            Font normal =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            12);

            document.add(new Paragraph(
        "==============================================",
        normal));

document.add(new Paragraph(
        "1. CALIDAD DEL REGISTRO DIARIO",
        titulo));

document.add(new Paragraph(" ", normal));

document.add(new Paragraph(
        "Estado: " + estado.getEstado(),
        normal));

        document.add(new Paragraph(
                "Calidad del registro: " +
                        estado.getPuntaje() + "%",
                normal));

        document.add(new Paragraph(
                "Registros completos: " +
                        estado.getRegistrosCompletos() +
                        " de " +
                        estado.getRegistrosIniciados(),
                normal));

        document.add(new Paragraph(
                estado.getDescripcion(),
                normal));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        
        document.add(new Paragraph(
        "==============================================",
        normal));

        document.add(new Paragraph(
                "2. INDICADORES DIARIOS",
                titulo));

        document.add(new Paragraph(" ", normal));

        document.add(new Paragraph(
                "Actividad registrada: "
                        + estadisticas.getActividadRegistrada(),
                normal));

        document.add(new Paragraph(
                "Registros completos: "
                        + estadisticas.getPresentes(),
                normal));

        document.add(new Paragraph(
                "Pendientes de egreso: "
                        + estadisticas.getSinEgreso(),
                normal));

        document.add(new Paragraph(
                "Sin registro: "
                        + estadisticas.getSinFirmaTotal(),
                normal));

        document.add(new Paragraph(
                "Porcentaje de asistencia: "
                        + estadisticas.getPorcentajeAsistencia() + "%",
                normal));

        document.add(new Paragraph(" "));
        
        

        document.close();

            return baos.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

    }

}