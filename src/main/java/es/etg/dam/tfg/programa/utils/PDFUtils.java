package es.etg.dam.tfg.programa.utils;

import es.etg.dam.tfg.programa.modelo.Videojuego;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PDFUtils {
    public static void exportarBiblioteca(List<Videojuego> juegos, String nombreUsuario, File archivo) throws IOException {
    try (PDDocument document = new PDDocument()) {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        float y = page.getMediaBox().getHeight() - 50;
        float margin = 50;
        float lineHeight = 15;

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, y);
        contentStream.showText("Biblioteca de Juegos de " + nombreUsuario);
        contentStream.endText();

        y -= lineHeight * 2;

        for (Videojuego juego : juegos) {
            if (y <= 50) {
                contentStream.close();
                page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                contentStream = new PDPageContentStream(document, page);
                y = page.getMediaBox().getHeight() - 50;
            }

            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, y);
            contentStream.showText("Nombre: " + juego.getNombre());
            contentStream.newLineAtOffset(0, -lineHeight);
            contentStream.showText("Consolas: " + TextoUtils.obtenerNombres(juego.getConsolas()));
            contentStream.newLineAtOffset(0, -lineHeight);
            contentStream.showText("Géneros: " + TextoUtils.obtenerNombres(juego.getGeneros()));
            contentStream.newLineAtOffset(0, -lineHeight);
            contentStream.showText("Empresa: " + (juego.getCompania() != null ? juego.getCompania().getNombre() : "N/A"));
            contentStream.endText();

            y -= lineHeight * 5;
        }

        contentStream.close();
        document.save(archivo);
    }
}

public static void exportarWishlist(List<Videojuego> juegos, String nombreUsuario, File archivo) throws IOException {
    try (PDDocument document = new PDDocument()) {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        float y = page.getMediaBox().getHeight() - 50;
        float margin = 50;
        float lineHeight = 15;

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, y);
        contentStream.showText("Wishlist de " + nombreUsuario);
        contentStream.endText();

        y -= lineHeight * 2;

        for (Videojuego juego : juegos) {
            if (y <= 50) {
                contentStream.close();
                page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                contentStream = new PDPageContentStream(document, page);
                y = page.getMediaBox().getHeight() - 50;
            }

            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, y);
            contentStream.showText("Nombre: " + juego.getNombre());
            contentStream.newLineAtOffset(0, -lineHeight);
            contentStream.showText("Consolas: " + TextoUtils.obtenerNombres(juego.getConsolas()));
            contentStream.newLineAtOffset(0, -lineHeight);
            contentStream.showText("Géneros: " + TextoUtils.obtenerNombres(juego.getGeneros()));
            contentStream.newLineAtOffset(0, -lineHeight);
            contentStream.showText("Empresa: " + (juego.getCompania() != null ? juego.getCompania().getNombre() : "N/A"));
            contentStream.endText();

            y -= lineHeight * 5;
        }

        contentStream.close();
        document.save(archivo);
    }
}

}

