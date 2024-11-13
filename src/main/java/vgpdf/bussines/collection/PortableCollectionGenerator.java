package vgpdf.bussines.collection;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.PDEmbeddedFilesNameTreeNode;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PageMode;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDEmbeddedFile;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import vgpdf.common.service.config.Constantes;

public class PortableCollectionGenerator {

	private static Logger log = Logger.getLogger(PortableCollectionGenerator.class.getName());
	
	public static void generatePortableCollection(Path directory, List<Path> pdfFiles) {
		log.debug("Generating portable collection for directory: " + directory);
		    
		try (PDDocument doc = new PDDocument()) {
		    PDPage page = new PDPage();
		    doc.addPage(page);

		     try (PDPageContentStream contentStream = new PDPageContentStream(doc, page)) {
		        contentStream.beginText();
		        contentStream.setFont(new PDType1Font(FontName.HELVETICA), 12);
		        contentStream.newLineAtOffset(100, 700);
		        contentStream.showText("Example of a portable collection");
		        contentStream.endText();
		    }
		    
		 // Generar y añadir miniaturas de cada archivo PDF
            List<File> thumbnailFiles = new ArrayList<>();
            for (Path pdfPath : pdfFiles) {
                File thumbnail = ThumbnailGenerator.generateThumbnail(pdfPath, 100, 100);
                thumbnailFiles.add(thumbnail);
            }

            // Añadir las miniaturas como una página de vista en el documento
            addThumbnailsToPDF(doc, thumbnailFiles);
		    
		
		    PDEmbeddedFilesNameTreeNode efTree = new PDEmbeddedFilesNameTreeNode();
		    Map<String, PDComplexFileSpecification> map = new HashMap<>();
		
		    for (Path pdfPath : pdfFiles) {
		        PDComplexFileSpecification fs = createPdfFileSpecification(pdfPath, doc);
		        map.put(fs.getFilename(), fs);
		    }
		
		    PDEmbeddedFilesNameTreeNode treeNode = new PDEmbeddedFilesNameTreeNode();
		    treeNode.setNames(map);
		    efTree.setKids(Collections.singletonList(treeNode));
		
		    PDDocumentNameDictionary names = new PDDocumentNameDictionary(doc.getDocumentCatalog());
		    names.setEmbeddedFiles(efTree);
		    doc.getDocumentCatalog().setNames(names);
		    doc.getDocumentCatalog().setPageMode(PageMode.USE_ATTACHMENTS);
		
		    /*COSDictionary collectionDic = createCollectionDictionary();
		    doc.getDocumentCatalog().getCOSObject().setItem(COSName.COLLECTION, collectionDic);
		    doc.getDocumentCatalog().setVersion("1.7");
		
		    for (PDComplexFileSpecification fs : map.values()) {
		        COSDictionary ciDict = createCollectionItemDictionary(fs);
		        fs.getCOSObject().setItem(COSName.CI, ciDict);
		    }*/
		
		    
		    Path output = Paths.get(Constantes.OUT_PATH + directory.getFileName().toString() + ".pdf");
		    
		    doc.save(output.toString());
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
	}
	 
 	private static PDComplexFileSpecification createPdfFileSpecification(Path path, PDDocument doc) throws IOException {
		 
		 PDComplexFileSpecification file = new PDComplexFileSpecification();
		 
		 String name = path.getFileName().toString();
		 
		 file.setFile(name);
		 file.setFileUnicode(name);

         byte[] data = Files.readAllBytes(path);
         PDEmbeddedFile ef1 = new PDEmbeddedFile(doc, new ByteArrayInputStream(data), COSName.FLATE_DECODE);
         ef1.setSubtype("application/pdf");
         ef1.setSize(data.length);
         ef1.setCreationDate(new GregorianCalendar());

         file.setEmbeddedFile(ef1);
         file.setEmbeddedFileUnicode(ef1);
         //file.setFileDescription("Archivo de acta 1");
		 
		 return file;
	 }

    private static COSDictionary createCollectionDictionary() {
        COSDictionary collectionDic = new COSDictionary();
        COSDictionary schemaDict = new COSDictionary();
        schemaDict.setItem(COSName.TYPE, COSName.COLLECTION_SCHEMA);
        COSDictionary sortDic = new COSDictionary();
        sortDic.setItem(COSName.TYPE, COSName.COLLECTION_SORT);
        sortDic.setString(COSName.A, "true"); // sort ascending
        sortDic.setItem(COSName.S, COSName.getPDFName("fieldtwo"));
        collectionDic.setItem(COSName.TYPE, COSName.COLLECTION);
        collectionDic.setItem(COSName.SCHEMA, schemaDict);
        collectionDic.setItem(COSName.SORT, sortDic);
        collectionDic.setItem(COSName.VIEW, COSName.D); // Details mode

        COSDictionary fieldDict1 = new COSDictionary();
        fieldDict1.setItem(COSName.TYPE, COSName.COLLECTION_FIELD);
        fieldDict1.setItem(COSName.SUBTYPE, COSName.S); // type: text field
        fieldDict1.setString(COSName.N, "field header one (description)"); // header text
        fieldDict1.setInt(COSName.O, 1); // order on the screen

        COSDictionary fieldDict2 = new COSDictionary();
        fieldDict2.setItem(COSName.TYPE, COSName.COLLECTION_FIELD);
        fieldDict2.setItem(COSName.SUBTYPE, COSName.S); // type: text field
        fieldDict2.setString(COSName.N, "field header two (name)");
        fieldDict2.setInt(COSName.O, 2);

        COSDictionary fieldDict3 = new COSDictionary();
        fieldDict3.setItem(COSName.TYPE, COSName.COLLECTION_FIELD);
        fieldDict3.setItem(COSName.SUBTYPE, COSName.N); // type: number field
        fieldDict3.setString(COSName.N, "field header three (size)");
        fieldDict3.setInt(COSName.O, 3);

        schemaDict.setItem("fieldone", fieldDict1); // field name (this is a key)
        schemaDict.setItem("fieldtwo", fieldDict2);
        schemaDict.setItem("fieldthree", fieldDict3);

        return collectionDic;
    }

    private static COSDictionary createCollectionItemDictionary(PDComplexFileSpecification fs) {
        COSDictionary ciDict = new COSDictionary();
        ciDict.setItem(COSName.TYPE, COSName.COLLECTION_ITEM);
        ciDict.setString("fieldone", fs.getFileDescription());
        ciDict.setString("fieldtwo", fs.getFile());
        ciDict.setInt("fieldthree", fs.getEmbeddedFile().getSize());
        return ciDict;
    }
    
    public static void addThumbnailsToPDF(PDDocument document, List<File> thumbnailFiles) throws IOException {
	    PDPage thumbnailPage = new PDPage();
	    document.addPage(thumbnailPage);
	
	    try (PDPageContentStream contentStream = new PDPageContentStream(document, thumbnailPage)) {
	        contentStream.beginText();
	        contentStream.setFont(new PDType1Font(FontName.HELVETICA), 12);
	        contentStream.newLineAtOffset(50, 750);
	        contentStream.showText("Miniaturas de Archivos Embebidos:");
	        contentStream.endText();
	
	        int x = 50;
	        int y = 700;
	        int thumbnailSize = 100;
	
	        for (File thumbnailFile : thumbnailFiles) {
	            PDImageXObject image = PDImageXObject.createFromFile(thumbnailFile.getAbsolutePath(), document);
	            contentStream.drawImage(image, x, y, thumbnailSize, thumbnailSize);
	
	            // Ajustar posición para la siguiente miniatura
	            x += thumbnailSize + 20;
	            if (x > 500) {
	                x = 50;
	                y -= thumbnailSize + 20;
	            }
	        }
	    }
	}
	        
}
	        
	        
	        
	        
	        
	        
	        
	
	        
	        
	        
	        
	        
        
        
      /*  for (Path pdf : pdfFiles) {
            // Agregar cada PDF a la colección
            System.out.println("Adding " + pdf.getFileName() + " to portable collection.");
        }
    } */
	 
	 
	 
	 
	 
