package ar.com.sdd.asignator.service.rest;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import ar.com.sdd.asignator.service.ConfigurationService;

@Path("/configuration")
public class ConfigurationRest {
	
	@GET
	@Produces("application/json")
	@Path("properties")
	public Map<String, String> getProperties() {
		return ConfigurationService.getInstance().getPropertiesMap();
	}

	@GET
	@Produces("application/json")
	@Path("path2")
	public String getSasa() {
		return "sasa";
	}

}
