package ar.com.sdd.asignator.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import ar.com.sdd.asignator.service.ConfigurationService;

public class MailToTicketJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		ConfigurationService configurationService = ConfigurationService.getInstance();
		
		//Crea un nuevo MailServiceLoopFolderExecutor
		//Instancia MailService
		
		//Dentro del executor instancia al JiraService y crea el jira
		
		configurationService.setProperty("tito", "puente");
		
		System.out.println("Jobazo2");
	}

}
