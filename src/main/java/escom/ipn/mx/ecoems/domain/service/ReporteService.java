package escom.ipn.mx.ecoems.domain.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import escom.ipn.mx.ecoems.domain.entity.ResultadoExamen;
import escom.ipn.mx.ecoems.domain.entity.Usuario;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class ReporteService {

    // Definición de Colores Institucionales (Acorde a tu sistema)
    public static final BaseColor AZUL_PRIMARIO = new BaseColor(30, 60, 114); // #1e3c72
    public static final BaseColor GRIS_TEXTO = new BaseColor(100, 116, 139);   // #64748b
    public static final BaseColor FONDO_TABLA = new BaseColor(248, 250, 252); // #f8fafc

    public ByteArrayInputStream generarHistorialPdf(Usuario usuario, List<ResultadoExamen> resultados) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // --- 1. ENCABEZADO TIPO DASHBOARD ---
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, AZUL_PRIMARIO);
            Paragraph titulo = new Paragraph("Reporte de Progreso Académico", fontTitulo);
            titulo.setAlignment(Element.ALIGN_LEFT);
            document.add(titulo);

            Font fontFecha = FontFactory.getFont(FontFactory.HELVETICA, 10, GRIS_TEXTO);
            Paragraph fechaEmision = new Paragraph("Emitido el: " + new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()), fontFecha);
            fechaEmision.setSpacingAfter(20f);
            document.add(fechaEmision);

            // --- 2. TARJETA DE INFORMACIÓN DEL ALUMNO ---
            PdfPTable infoTable = new PdfPTable(1);
            infoTable.setWidthPercentage(100);
            
            PdfPCell infoCell = new PdfPCell();
            infoCell.setPadding(15f);
            infoCell.setBackgroundColor(FONDO_TABLA);
            infoCell.setBorder(Rectangle.NO_BORDER);
            
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, GRIS_TEXTO);
            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

            infoCell.addElement(new Phrase("ESTUDIANTE", labelFont));
            infoCell.addElement(new Phrase(usuario.getNombre() + " " + usuario.getApPaterno() + " " + usuario.getApMaterno(), dataFont));
            infoCell.addElement(new Paragraph(" "));
            infoCell.addElement(new Phrase("CORREO ELECTRÓNICO", labelFont));
            infoCell.addElement(new Phrase(usuario.getCorreo(), dataFont));

            infoTable.addCell(infoCell);
            document.add(infoTable);
            document.add(Chunk.NEWLINE);

            // --- 3. TABLA DE RESULTADOS ESTILIZADA ---
            PdfPTable table = new PdfPTable(3); 
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setWidths(new float[]{3, 2, 1}); // Proporción de columnas

            // Encabezados con estilo
            addTableHeader(table, "EXAMEN / ASIGNATURA");
            addTableHeader(table, "FECHA DE INTENTO");
            addTableHeader(table, "PUNTAJE");

            // Formato de fecha
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            // Llenar datos con estilo de celdas
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.DARK_GRAY);

            for (ResultadoExamen resultado : resultados) {
                table.addCell(createStyledCell(resultado.getExamen().getNombre(), cellFont, Element.ALIGN_LEFT));
                table.addCell(createStyledCell(sdf.format(resultado.getFecha()), cellFont, Element.ALIGN_CENTER));
                
                // Color dinámico según calificación
                Font scoreFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, 
                    resultado.getScoreTotal() >= 6 ? new BaseColor(25, 135, 84) : new BaseColor(220, 53, 69));
                
                table.addCell(createStyledCell(resultado.getScoreTotal().toString() + "/10", scoreFont, Element.ALIGN_CENTER));
            }

            document.add(table);

            // --- 4. PIE DE PÁGINA ---
            Paragraph footer = new Paragraph("\n\nEste reporte es generado automáticamente por el sistema ECOEMS.", 
                FontFactory.getFont(FontFactory.HELVETICA, 8, GRIS_TEXTO));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addTableHeader(PdfPTable table, String headerTitle) {
        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.WHITE);
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(AZUL_PRIMARIO);
        header.setBorder(Rectangle.NO_BORDER);
        header.setPadding(10f);
        header.setPhrase(new Phrase(headerTitle, fontHeader));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(header);
    }

    private PdfPCell createStyledCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(8f);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(alignment);
        cell.setBorderColor(new BaseColor(226, 232, 240)); // #e2e8f0
        return cell;
    }
}