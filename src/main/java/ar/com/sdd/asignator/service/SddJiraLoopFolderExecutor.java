package ar.com.sdd.asignator.service;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.apache.log4j.Logger;



public class SddJiraLoopFolderExecutor implements MailServiceLoopFolderExecutor {

	private static final Logger log = Logger.getLogger(SddJiraLoopFolderExecutor.class);

	@Override
	public void mailExecutor(Message message) throws MessagingException {
		log.debug("Ejectuando mensaje [" + message + "]");
		
		System.out.println(message.getSubject());
		
	}

}
