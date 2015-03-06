package ar.com.sdd.asignator.service;

import javax.mail.Message;

public class MailService {

	public void loopFolder(MailServiceLoopFolderExecutor loopExecutor) {
		
		//se conecta
		
		//loopea y llama a mailexecutor
			Message msg = null;
			loopExecutor.mailExecutor(msg);
		//se desconecta
		//hace commit si se lo piden por parametro
		
	}
	
}
