package vgpdf.bussines.file;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import vgpdf.bussines.collection.PortableCollectionGenerator;

public class PdfFileVisitor extends SimpleFileVisitor<Path> {
	
	private static Logger log = Logger.getLogger(PortableCollectionGenerator.class.getName());
	private final static boolean DEBUG = false;
	
    private Map<Path, List<Path>> pdfFilesByDirectory = new HashMap<>();
    

    public void collectPdfFiles(Path startPath) {
        try {
            Files.walkFileTree(startPath, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<Path, List<Path>> getPdfFilesByDirectory() {
        return pdfFilesByDirectory;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        pdfFilesByDirectory.put(dir, new ArrayList<>());
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (file.toString().endsWith(".pdf")) {
            Path parentDir = file.getParent();
            pdfFilesByDirectory.get(parentDir).add(file);
            if(DEBUG) log.debug("Found PDF: " + file);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
    	if(DEBUG) log.debug("Failed to access file: " + file + " (" + exc.getMessage() + ")");
        return FileVisitResult.CONTINUE;
    }
}
