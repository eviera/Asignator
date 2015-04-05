package ar.com.sdd.asignator.service.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

@Path("/scheduler")
public class SchedulerRest {

	private final Logger log = Logger.getLogger(getClass());

	private static final String ASIGNATOR_SCHEDULER = "AsignatorScheduler";

	@GET
	@Produces("application/json")
	public SchedulerStatusResponse getStatus() {
		log.info("Consultando estado del scheduler");
		SchedulerStatusResponse schedResp = new SchedulerStatusResponse();
		schedResp.successful = true;
		try {
			Scheduler scheduler = getScheduler();
			if (scheduler == null || scheduler.isShutdown() || scheduler.isInStandbyMode()) {
				schedResp.status = "off";
			} else if (scheduler.isStarted()) {
				schedResp.status = "on";
			} else {
				schedResp.successful = false;
				schedResp.status = "error";
				schedResp.errorMessage = "Error al consultar estado de la tarea";
				log.error("No deberia haber llegado aqui");
			}
			log.info("El scheduler esta en estado [" + schedResp.status + "]");
			return schedResp;

		} catch (SchedulerException e) {
			throw new RuntimeException("Error al obtener el status del scheduler", e);
		}
	}

	@POST
	public SchedulerStatusResponse start() {
		log.info("Iniciando scheduler");
		try {
			Scheduler scheduler = getScheduler();
			if (scheduler == null || scheduler.isShutdown() || scheduler.isInStandbyMode()) {
				scheduler.start();
			} else {
				log.info("Ya esta iniciado");
			}
			return getStatus();
		} catch (SchedulerException e) {
			throw new RuntimeException("Error al iniciar el scheduler", e);
		}
	}

	@DELETE
	public SchedulerStatusResponse stop() {
		log.info("Parando scheduler (standby)");
		try {
			Scheduler scheduler = getScheduler();
			if (scheduler != null && scheduler.isStarted() && !scheduler.isInStandbyMode()) {
				scheduler.standby();
			} else {
				log.info("Ya esta parado");
			}
			return getStatus();
		} catch (SchedulerException e) {
			throw new RuntimeException("Error al parar el scheduler", e);
		}
	}

	private Scheduler getScheduler() throws SchedulerException {
		StdSchedulerFactory fact = new StdSchedulerFactory();
		Scheduler scheduler = fact.getScheduler(ASIGNATOR_SCHEDULER);
		return scheduler;
	}
	
	public static class SchedulerStatusResponse extends RestResponse {
		public String status;
	}
	
}
