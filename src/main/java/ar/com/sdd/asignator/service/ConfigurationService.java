package ar.com.sdd.asignator.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ConfigurationService {

	private static final String ASIGNATOR_PROPERTIES = "/asignator.properties";

	private final Logger log = Logger.getLogger(getClass());
	
	private static ConfigurationService instance = null;
	
	private Properties props;
	
	private ConfigurationService() {
		//Levanto las properties
		InputStream propStream = ConfigurationService.class.getResourceAsStream(ASIGNATOR_PROPERTIES);
		props = new Properties();
		try {
			props.load(propStream);
		} catch (IOException e) {
			log.error("Error al levantar las properties", e);
		}
	}
	
	public static ConfigurationService getInstance() {
		if (instance == null) {
			instance = new ConfigurationService();
		}
		return instance;
	}

	public Map<String, String> getPropertiesMap() {
		Map<String, String> map = new HashMap<String, String>();
		for (String key : props.stringPropertyNames()) {
		    map.put(key, props.getProperty(key));
		}
		return map;
	}
	
	public String getProperty(String key) {
		return props.getProperty(key);
	}

	public void setProperty(String key, String value) {
		props.put(key, value);
		//updateProperties();
	}
	
	private void updateProperties() {
		//TODO revisar esto porque no funciona
		// java.lang.IllegalArgumentException: URI scheme is not "file"
		
		URL url = ConfigurationService.class.getResource(ASIGNATOR_PROPERTIES);
		try {
			props.store(new FileOutputStream(new File(url.toURI())), null);
		} catch (Exception e) {
			throw new RuntimeException("Problema al guardar las properties", e);
		}		
	}
}
