package ar.com.sdd.asignator.service;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

public interface MailServiceLoopFolderExecutor {

	public void mailExecutor(Folder folder, Message message) throws MessagingException;
	
}
