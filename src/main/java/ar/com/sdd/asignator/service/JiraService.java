package ar.com.sdd.asignator.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class JiraService {
	
	private final Logger log = Logger.getLogger(getClass());	

	private ConfigurationService configurationService;
	
	public JiraService() {
		configurationService = ConfigurationService.getInstance();
	}
	
	@SuppressWarnings("unchecked")
	public String createNewTicketForMessage(Message message) {
		log.info("Creando un nuevo ticket");
		try {
			XmlRpcClient rpcClient = getClient();
			log.info("Logueandose");
			String loginToken = getLoginToken(rpcClient);
			if (StringUtils.isEmpty(loginToken)) {
				throw new RuntimeException("Problema al loguearse al jira, el loginToken volvio vacio");
			}
			
			String assignee = configurationService.getProperty("jira_issue_assignee");
			if (StringUtils.isEmpty(assignee)) {
				throw new RuntimeException("El jira_issue_assignee esta vacio");
			}
			
			Hashtable<String, Object> struct = new Hashtable<String, Object>();
			//Parametros necesarios para poder crear el ticket
			struct.put("summary", message.getSubject());
			struct.put("description", getText(message));
			struct.put("project", "TKT");
			struct.put("type", "3"); //3=Task
			struct.put("versions", "");
			struct.put("assignee", assignee);
			struct.put("priority", "7");	//7=PA (o N/A)
			
			struct.put("customFieldValues", Arrays.asList(
					makeCustomFieldHashtable("customfield_10064", assignee),	//Solicitante 
					makeCustomFieldHashtable("customfield_10060", "10040"),		//Origen (10040=Interno, 10041=Externo)
					makeCustomFieldHashtable("customfield_10040", "10030"))); 	//Riesgo (10030=N/A, 10031=Bajo, 10032=Mendio, 10033=Alto)
	
			log.info("Creando issue para el mail de subject [" + message.getSubject() + "]");
			HashMap<String, String> creationResult = (HashMap<String, String>)rpcClient.execute("jira1.createIssue", Arrays.asList(loginToken, struct));
			if (creationResult == null || creationResult.size() == 0) {
				throw new RuntimeException("No se pudo crear el issue en jira. El creationResult volvio vacio");
			}
			String key = creationResult.get("key");
			if (StringUtils.isEmpty(key)) {
				throw new RuntimeException("No se pudo crear el issue en jira, no devolvio un key. El creationResult es [" + creationResult + "]");
			}
			log.info("El issue fue creado con la key [" + key + "]");
			
			log.info("Haciendo logout");
			logout(rpcClient, loginToken);
			
			return key;
		} catch (Exception e) {
			throw new RuntimeException("Error al ejecutar el servicio de jira", e);
		}
		
	}

	private Boolean logout(XmlRpcClient rpcClient, String loginToken) throws XmlRpcException {
		return (Boolean) rpcClient.execute("jira1.logout", Arrays.asList(loginToken));		
	}

	private String getLoginToken(XmlRpcClient rpcClient) throws XmlRpcException {
		String user = configurationService.getProperty("jira_server_user");
		String pass = configurationService.getProperty("jira_server_pass");
		String loginToken = (String) rpcClient.execute("jira1.login", Arrays.asList(user, pass));
		return loginToken;
	}

	
	private XmlRpcClient getClient() throws MalformedURLException {
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		String host = configurationService.getProperty("jira_server_host");
		config.setServerURL(new URL(host));
		config.setEnabledForExtensions(true);
		config.setConnectionTimeout(60 * 1000);
		config.setReplyTimeout(60 * 1000);

		// Initialise RPC Client
		XmlRpcClient rpcClient = new XmlRpcClient();
		rpcClient.setConfig(config);
		return rpcClient;
	}

    private static  Hashtable<String, Object> makeCustomFieldHashtable(String customFieldId, String value) {
        Hashtable<String, Object> t = new Hashtable<String, Object>();
        t.put("customfieldId", customFieldId);
        t.put("values", Arrays.asList(value));
        return t;
    }

    
	private String getText(Part part) throws MessagingException, IOException {
		if (part.isMimeType("text/*") || part.isMimeType("text/html")) {
			return (String) part.getContent();
			
		} else	if (part.isMimeType("multipart/alternative")) {
			// prefer html text over plain text
			Multipart mp = (Multipart) part.getContent();
			String text = null;
			for (int i = 0; i < mp.getCount(); i++) {
				Part bp = mp.getBodyPart(i);
				if (bp.isMimeType("text/plain")) {
					if (text == null) {
						text = getText(bp);
					}
					continue;
				} else if (bp.isMimeType("text/html")) {
					String s = getText(bp);
					if (s != null) {
						return s;
					}
				} else {
					return getText(bp);
				}
			}
			return text;
		} else if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				String s = getText(mp.getBodyPart(i));
				if (s != null) {
					return s;
				}
			}
		}

		throw new RuntimeException("No se puede obtener el cuerpo del mensaje de la parte [" + part + "]");
	}
}
