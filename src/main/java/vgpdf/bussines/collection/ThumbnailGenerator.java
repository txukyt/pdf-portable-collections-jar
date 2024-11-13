package vgpdf.bussines.collection;

import org.apache.pdfbox.rendering.PDFRenderer;

import vgpdf.common.service.config.Constantes;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ThumbnailGenerator {
    public static File generateThumbnail(Path pdfPath, int width, int height) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdfPath.toFile())) {
        	
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage image = renderer.renderImageWithDPI(0, 72);

            // Escalar la imagen a la miniatura deseada
            BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            thumbnail.getGraphics().drawImage(image, 0, 0, width, height, null);

            // Guardar la miniatura como imagen temporal
            File thumbnailFile = new File(Constantes.THUMBNAILS_PATH + "temp_thumbnail_" + pdfPath.getFileName() + ".png");
            ImageIO.write(thumbnail, "PNG", thumbnailFile);
            return thumbnailFile;
        }
    }
}
