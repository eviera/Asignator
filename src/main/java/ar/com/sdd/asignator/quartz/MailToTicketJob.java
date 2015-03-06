package ar.com.sdd.asignator.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MailToTicketJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//Crea un nuevo MailServiceLoopFolderExecutor
		//Instancia MailService
		
		//Dentro del executor instancia al JiraService y crea el jira
		
		System.out.println("Jobazo");
	}

}
