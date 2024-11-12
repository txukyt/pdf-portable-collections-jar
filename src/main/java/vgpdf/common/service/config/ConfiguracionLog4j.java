package vgpdf.common.service.config;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import J00.common.properties.LeerProperties;

public class ConfiguracionLog4j {

	private static Logger log;	
	private static LeerProperties properties = new LeerProperties(Constantes.CONFIG_BUNDLE_NAME);

	public static Logger inicializarLog() {			
		if (log == null){
			//Configurando log 	
			String urlFicheroConfiguracion= properties.getProperty("app.log.ficheroConfiguracion");
			PropertyConfigurator.configure(urlFicheroConfiguracion);
			log = Logger.getLogger(ConfiguracionLog4j.class.getName());
			log.debug(".....log de pdf-portable-collections configurado en base al archivo " + urlFicheroConfiguracion ) ;
		}
		else{
			//El log ya está configurado
			log.debug("log:log de pdf-portable-collections ya estaba configurado") ;
		}
		return log;
	}
}

