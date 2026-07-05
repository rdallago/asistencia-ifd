package com.instituto.asistencia.exportador;

import com.instituto.asistencia.dto.ReporteAsistenciaDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ExcelExporter {

    public static byte[] exportar(
            List<ReporteAsistenciaDTO> datos)
            throws Exception {

        Workbook wb = new XSSFWorkbook();

        Sheet hoja = wb.createSheet("Asistencias");

        Row encabezado = hoja.createRow(0);

        encabezado.createCell(0).setCellValue("DNI");
        encabezado.createCell(1).setCellValue("Docente");
        encabezado.createCell(2).setCellValue("Turno");
        encabezado.createCell(3).setCellValue("Ingreso");
        encabezado.createCell(4).setCellValue("Egreso");
        encabezado.createCell(5).setCellValue("Estado");

        int fila = 1;

        for (ReporteAsistenciaDTO r : datos) {

            Row row = hoja.createRow(fila++);

            row.createCell(0).setCellValue(r.getDni());

            row.createCell(1).setCellValue(r.getDocente());

            row.createCell(2).setCellValue(r.getTurno());

            row.createCell(3).setCellValue(
                    r.getIngreso() == null ? "" : r.getIngreso().toString());

            row.createCell(4).setCellValue(
                    r.getEgreso() == null ? "" : r.getEgreso().toString());

            row.createCell(5).setCellValue(r.getEstado());
        }

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();

        wb.write(out);

        wb.close();

        return out.toByteArray();
    }

}