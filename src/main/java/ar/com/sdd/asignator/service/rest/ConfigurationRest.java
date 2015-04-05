package ar.com.sdd.asignator.service.rest;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;

import ar.com.sdd.asignator.service.ConfigurationService;

@Path("/configuration")
public class ConfigurationRest {
	
	private final Logger log = Logger.getLogger(getClass());
	
	@GET
	@Produces("application/json")
	public Map<String, String> getProperties() {
		log.info("Cargando properties");
		return ConfigurationService.getInstance().getPropertiesMap();
	}

	@POST
	public void setProperties(Map<String, String> propertiesMap) {
		log.info("Guardando properties");
		ConfigurationService.getInstance().setPropertiesMap(propertiesMap);
	}
}
