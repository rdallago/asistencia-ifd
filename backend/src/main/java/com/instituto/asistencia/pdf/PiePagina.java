package com.instituto.asistencia.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PiePagina extends PdfPageEventHelper {

    private PdfTemplate total;

    private BaseFont baseFont;

    private final Font fuentePie =
            FontFactory.getFont(
                    FontFactory.HELVETICA,
                    8);

    @Override
    public void onOpenDocument(
            PdfWriter writer,
            Document document) {

        total = writer.getDirectContent()
                .createTemplate(30, 12);

        try {

            baseFont = BaseFont.createFont(
                    BaseFont.HELVETICA,
                    BaseFont.WINANSI,
                    BaseFont.NOT_EMBEDDED);

        } catch (Exception e) {

            throw new RuntimeException(e);

        }
    }

    @Override
    public void onEndPage(
            PdfWriter writer,
            Document document) {

        PdfContentByte cb =
                writer.getDirectContent();

        Rectangle page =
                document.getPageSize();

        String fechaHora =
                LocalDateTime.now()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "dd/MM/yyyy HH:mm"));

        // -------------------------
        // Línea superior del pie
        // -------------------------

        cb.moveTo(
                document.left(),
                document.bottom() - 5);

        cb.lineTo(
                document.right(),
                document.bottom() - 5);

        cb.stroke();

        // -------------------------
        // Sistema
        // -------------------------

        ColumnText.showTextAligned(

                cb,

                Element.ALIGN_LEFT,

                new Phrase(
                        "Sistema Integral de Asistencia Docente",
                        fuentePie),

                document.left(),

                document.bottom() - 18,

                0);

        // -------------------------
        // Instituto
        // -------------------------

        ColumnText.showTextAligned(

                cb,

                Element.ALIGN_LEFT,

                new Phrase(
                        "Instituto Superior de Formación Docente y Técnica",
                        fuentePie),

                document.left(),

                document.bottom() - 30,

                0);

        // -------------------------
        // Fecha
        // -------------------------

        ColumnText.showTextAligned(

                cb,

                Element.ALIGN_RIGHT,

                new Phrase(
                        "Emitido: " + fechaHora,
                        fuentePie),

                document.right(),

                document.bottom() - 18,

                0);

        // -------------------------
        // Página X/
        // -------------------------

        String texto =
                "Página "
                        + writer.getPageNumber()
                        + "/";

        float ancho =
                baseFont.getWidthPoint(
                        texto,
                        8);

        ColumnText.showTextAligned(

                cb,

                Element.ALIGN_RIGHT,

                new Phrase(
                        texto,
                        fuentePie),

                document.right() - 20,

                document.bottom() - 30,

                0);

        cb.addTemplate(

                total,

                document.right() - 20 + ancho,

                document.bottom() - 30);
    }

    @Override
    public void onCloseDocument(
            
            
            PdfWriter writer,
            Document document) {
System.out.println(">>> onCloseDocument() ejecutado");
        total.beginText();

        total.setFontAndSize(
                baseFont,
                8);

        total.setTextMatrix(
                0,
                0);

        total.showText(
                String.valueOf(
                        writer.getPageNumber() - 1));

        total.endText();
    }
}