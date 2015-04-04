package ar.com.sdd.asignator.service.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

@Path("/scheduler")
public class SchedulerRest {

	private static final Logger log = Logger.getLogger(SchedulerRest.class);

	@GET
	@Produces("application/json")
	public SchedulerStatusResponse getStatus() {
		log.info("Consultando estado del scheduler");
		try {
			String status = "";
			StdSchedulerFactory fact = new StdSchedulerFactory();
			Scheduler scheduler = fact.getScheduler("AsignatorScheduler");
			if (scheduler.isShutdown() || scheduler.isInStandbyMode()) {
				status = "off";
			} else if (scheduler.isStarted()) {
				status = "on";
			} else {
				status = "error";
				log.error("No deberia haber llegado aqui");
			}
			log.info("El scheduler esta [" + status + "]");
			SchedulerStatusResponse schedResp = new SchedulerStatusResponse();
			schedResp.status = status;
			return schedResp;

		} catch (SchedulerException e) {
			throw new RuntimeException("Error al obtener el status del scheduler", e);
		}
	}

	public static class SchedulerStatusResponse {
		public String status;
	}
}
