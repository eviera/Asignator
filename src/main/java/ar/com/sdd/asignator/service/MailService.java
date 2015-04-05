package ar.com.sdd.asignator.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class MailService {

	private final Logger log = Logger.getLogger(getClass());
	
	public void loopFolder(MailServiceLoopFolderExecutor loopExecutor) {
		
		log.info("Entrando en el loopFolder");
		
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
			String blackList = configurationService.getProperty("mail_blacklist");
			Set<String> blackListSet = new HashSet<>();
			if (!StringUtils.isEmpty(blackList)) {
				blackListSet.addAll(Arrays.asList(blackList.split("[,]")));
			}
			
			log.info("Conectandose al mail con host [" + host + "], port [" + port + "], user [" + user + "], pass [" + pass + "], folderPath [" + folderPath + "], "
					+ "blackList [" + blackList + "]");
			
			store.connect(host, port, user, pass);
			
			//Levanta el folder
			folder = store.getFolder(folderPath);
			folder.open(Folder.READ_WRITE);
			Message messages[] = folder.getMessages();
			
			//Loopea por los mensajes y llama al mailExecutor por cada uno
			boolean anyProcessed = false;
			for (Message message : messages) {
				log.info("Procesando mail de [" + message.getFrom()[0].toString() + "] con subject [" + message.getSubject() + "]");
				//Chequeo la blacklist
				boolean blackListed = false;
				if (!blackListSet.isEmpty()) {
					Address[] froms = message.getFrom();
					for (Address address : froms) {
						if (address instanceof InternetAddress && blackListSet.contains(((InternetAddress)address).getAddress())) {
							blackListed = true;
							break;
						}
					}
				}
				if (!blackListed) {
					loopExecutor.mailExecutor(folder, message);
					anyProcessed = true;
				} else {
					log.info("El remitente esta en la blackList, no se procesa el mail");
				}
			}

			//Commit del folder si se proceso algun mensaje
			if (anyProcessed) {
				log.info("Se procesaron mensajes y se hace commit del folder (expunge)");
				folder.expunge();
			}
				
				
		} catch (Exception e) {
			throw new RuntimeException("Ocurrio un error en el loopFolder", e);
		} finally {
			if (folder != null) {
				try {
					folder.close(true);
				} catch (MessagingException e) {
					throw new RuntimeException("Error al cerrar el folder", e);
				}
			}
			if (store != null) {
				try {
					store.close();
				} catch (MessagingException e) {
					throw new RuntimeException("Error al cerrar el store", e);
				}
			}
		}
		
	}
	
}
