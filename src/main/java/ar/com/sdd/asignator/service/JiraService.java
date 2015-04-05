package ar.com.sdd.asignator.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

import javax.mail.Message;

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
	
	public String createNewTicketForMessage(Message message) {
		log.info("Creando un nuevo ticket");
		String result = null;
		try {
			XmlRpcClient rpcClient = getClient();
			String loginToken = getLoginToken(rpcClient);
			
			Hashtable<String, Object> struct = new Hashtable<String, Object>();
			//Parametros necesarios para poder crear el ticket
			struct.put("summary", message.getSubject());
			
			
			//TODO ver como extraer el contenido del mensaje
			
			
			struct.put("description", "Descripcion \n\nh2.Pruebas\n- uno\n- dos\n- (/) tres");
			struct.put("project", "TKT");
			struct.put("type", "3"); //3=Task
			struct.put("versions", "TRUNK D8");
			struct.put("assignee", "eviera");
			struct.put("priority", "7");
			
			
			/*
			 * Saque de requiered en TKT: timetracking y components
			 */
			//struct.put("timetracking", "1h");
			struct.put("components", Arrays.asList(makeCustomFieldHashtable("10044", "10044")));
			struct.put("customFieldValues", Arrays.asList(
					makeCustomFieldHashtable("customfield_10064", "eviera"), 
					makeCustomFieldHashtable("customfield_10060", "10040"),
					makeCustomFieldHashtable("customfield_10040", "10030"))); //Solicitante
	
			HashMap<String, String> creationResult = (HashMap<String, String>)rpcClient.execute("jira1.createIssue", Arrays.asList(loginToken, struct));
			System.out.println(creationResult);
			System.out.println("key creado=" + creationResult.get("key"));
	
			logout(rpcClient, loginToken);
			
			return result;
		} catch (Exception e) {
			throw new RuntimeException("Error al ejecutar el servicio de jira", e);
		}
		
	}

	private void logout(XmlRpcClient rpcClient, String loginToken) throws XmlRpcException {
		Boolean result = (Boolean) rpcClient.execute("jira1.logout", Arrays.asList(loginToken));		
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
	
}
