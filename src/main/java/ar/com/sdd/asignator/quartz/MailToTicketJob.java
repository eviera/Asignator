package ar.com.sdd.asignator.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import ar.com.sdd.asignator.service.MailService;
import ar.com.sdd.asignator.service.SddJiraLoopFolderExecutor;

public class MailToTicketJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		//Creo una nueva instancia del que va a examinar cada mail y decidir que hacer
		SddJiraLoopFolderExecutor sddJiraExecutor = new SddJiraLoopFolderExecutor();

		MailService mailService = new MailService();
		//Llamo al servicio de mail para que se conecte al folder y por cada mail llame al executor
		mailService.loopFolder(sddJiraExecutor);
		
	}

}
