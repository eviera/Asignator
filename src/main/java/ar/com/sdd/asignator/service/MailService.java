package ar.com.sdd.asignator.service;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

public class MailService {

	public void loopFolder(MailServiceLoopFolderExecutor loopExecutor) {

		ConfigurationService configurationService = ConfigurationService.getInstance();
		Folder folder = null;
		Store store = null;
		try {
		
			//Se conecta al servidor
			Properties props = System.getProperties();
			props.setProperty("mail.store.protocol", "imaps");
			props.put("mail.imaps.ssl.trust", "*");
			Session session = Session.getDefaultInstance(props, null);
			store = session.getStore("imaps");
			
			String host = configurationService.getProperty("mail_server_host");
			Integer port = Integer.valueOf(configurationService.getProperty("mail_server_port"));
			String user = configurationService.getProperty("mail_server_user");
			String pass = configurationService.getProperty("mail_server_pass");
			String folderPath = configurationService.getProperty("mail_server_folder");
			
			store.connect(host, port, user, pass);
			
			//Levanta el folder
			folder = store.getFolder(folderPath);
			folder.open(Folder.READ_WRITE);
			Message messages[] = folder.getMessages();
			
			//Loopea por los mensajes y llama al mailExecutor por cada uno
			for (Message message : messages) {
				loopExecutor.mailExecutor(message);
			}
			
			// TODO hacer de aca para abajo
			
			//se desconecta
			//hace commit si se lo piden por parametro
				
				
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (folder != null) {
				try {
					folder.close(true);
				} catch (MessagingException e) {
					throw new RuntimeException(e);
				}
			}
			if (store != null) {
				try {
					store.close();
				} catch (MessagingException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
	}
	
}
