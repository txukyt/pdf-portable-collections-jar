package vgpdf.common.service.config;

import J00.common.properties.LeerProperties;

public class Constantes {
	
	public static String CONFIG_BUNDLE_NAME  = "pdf-portable-collections/pdf-portable-collections";	

	private static LeerProperties properties = new LeerProperties(Constantes.CONFIG_BUNDLE_NAME);
	
	public static String IN_PATH = properties.getProperty("app.pdf.path").trim();
	
	public static String OUT_PATH = properties.getProperty("app.collection.path").trim();
	
	

}
