package ar.com.sdd.asignator.service;

import javax.mail.Message;
import javax.mail.MessagingException;

public interface MailServiceLoopFolderExecutor {

	public void mailExecutor(Message message) throws MessagingException;
	
}
