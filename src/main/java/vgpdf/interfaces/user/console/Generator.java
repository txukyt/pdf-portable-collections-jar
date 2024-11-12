package vgpdf.interfaces.user.console;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import vgpdf.bussines.file.PdfFileVisitor;
import vgpdf.bussines.collection.PortableCollectionGenerator;

import vgpdf.common.service.config.ConfiguracionLog4j;
import vgpdf.common.service.config.Constantes;

public class Generator {
	
	private static Logger log = Logger.getLogger(Generator.class.getName());
	
	private Generator() {}
	
	public static void main(String[] args) {
		try {
			Generator app = new Generator();
			
			ConfiguracionLog4j.inicializarLog();		
			log.debug("#### PDF PORTABLE COLLECTIONS GENERATOR #### START ####");			
			
			Map<Path, List<Path>> pdfFilesByDirectory = app.getPdfFilesByDirectory(Constantes.IN_PATH);
			
			for (Map.Entry<Path, List<Path>> entry : pdfFilesByDirectory.entrySet()) {				
				List<Path> pdfFiles = entry.getValue();				
				if(!pdfFiles.isEmpty() && pdfFiles.size() > 0) {
				    Path directory = entry.getKey();
				    PortableCollectionGenerator.generatePortableCollection(directory, pdfFiles);
				}
			}
			
		} catch (RuntimeException e) {
			log.error("ERROR ### " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.error("ERROR ### Se ha producido algún error genérico.");
			e.printStackTrace();
		} finally {
			log.debug("#### PDF PORTABLE COLLECTIONS GENERATOR #### END ####");
	        System.exit(0);
		}
	}
	
	/**
	 * Obtiene un mapa de archivos PDF organizados por directorios a partir de una ruta.
	 * 
	 * @param path La ruta del directorio inicial desde donde se comenzará la búsqueda de archivos PDF.
	 * @return Un mapa donde las claves son directorios (de tipo `Path`) y los valores son listas de rutas
	 *         de archivos PDF (`List<Path>`) encontrados en esos directorios.
	 */
	private Map<Path, List<Path>> getPdfFilesByDirectory(String path) {
	    Path startPath = Paths.get(path); 
	    PdfFileVisitor visitor = new PdfFileVisitor();
	    
	    visitor.collectPdfFiles(startPath);
	    
	    return visitor.getPdfFilesByDirectory();
	}
}