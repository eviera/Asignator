package ar.com.sdd.asignator.service;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;



public class SddJiraLoopFolderExecutor implements MailServiceLoopFolderExecutor {

	private final Logger log = Logger.getLogger(getClass());

	private static final String TKT_SIGNATURE = "TKT-";

	@Override
	public void mailExecutor(Folder folder, Message message) throws MessagingException {
		//Si el subject dice TKT- lo salto
		if (!message.getSubject().contains(TKT_SIGNATURE)) {
			//Copio el mail
			log.info("Se copia el mail con subject [" + message.getSubject() + "]");
			MimeMessage messageCopy = new MimeMessage((MimeMessage)message);
			
			//Llamo al servicio de jira y creo un jira nuevo
			JiraService jiraService = new JiraService();
			String ticketNumber = jiraService.createNewTicketForMessage(message);
			if (ticketNumber == null) {
				throw new RuntimeException("Hubo un error al intentar crear el ticket");
			}
			
			//Le asigno el subject al mail copiado y lo agrego al folder
			messageCopy.setSubject("[" + ticketNumber + "] " + message.getSubject() );
			folder.appendMessages(new Message[]{ messageCopy }); 
			messageCopy.saveChanges();
			//Borro el mail original
			message.setFlag(Flags.Flag.DELETED, true);
			
		} else {
			log.debug("Mensaje salteado porque ya esta asignado");
		}
		
	}

}
