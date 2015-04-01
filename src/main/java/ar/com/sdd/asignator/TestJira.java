package ar.com.sdd.asignator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Scanner;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

//http://crunchify.com/create-very-simple-jersey-rest-service-and-send-json-data-from-java-client/
//http://www.mkyong.com/webservices/jax-rs/restfull-java-client-with-java-net-url/
//Usar Gson con https://developer.atlassian.com/static/rest/jira/4.4.1.html#id150510

//con SOAP
//http://www.sdd.com.ar/jira/rpc/soap/jirasoapservice-v2?wsdl
//http://stackoverflow.com/questions/19502158/creating-a-soap-client-in-java-given-a-wsdl-file

//Con RPC
//Ejemplo de: https://answers.atlassian.com/questions/73064/creating-an-issue-through-xml-rpc
//API: https://docs.atlassian.com/rpc-jira-plugin/4.4/com/atlassian/jira/rpc/xmlrpc/JiraXmlRpcService.html

public class TestJira {

	/*
	 * public static void doitJersey() { try {
	 * 
	 * JerseyJiraRestClientFactory f = new JerseyJiraRestClientFactory();
	 * JiraRestClient jc = f.createWithBasicHttpAuthentication(new
	 * URI("http://www.sdd.com.ar/jira"), "eviera", "");
	 * 
	 * SearchResult r =
	 * jc.getSearchClient().searchJql("type = Epic ORDER BY RANK ASC", null);
	 * 
	 * Iterator<BasicIssue> it = r.getIssues().iterator(); while (it.hasNext())
	 * {
	 * 
	 * Issue issue = jc.getIssueClient().getIssue(((BasicIssue)
	 * it.next()).getKey(), null);
	 * 
	 * System.out.println("Epic: " + issue.getKey() + " " + issue.getSummary());
	 * 
	 * Iterator<IssueLink> itLink = issue.getIssueLinks().iterator(); while
	 * (itLink.hasNext()) {
	 * 
	 * IssueLink issueLink = (IssueLink) itLink.next(); Issue issueL =
	 * jc.getIssueClient().getIssue((issueLink).getTargetIssueKey(), null);
	 * 
	 * System.out.println(issueLink.getIssueLinkType().getDescription() + ": " +
	 * issueL.getKey() + " " + issueL.getSummary() + " " +
	 * issueL.getFieldByName("Story Points").getValue());
	 * 
	 * } } } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * }
	 */

	public static void doitHTTP() {
		// Login
		/*
		 * try { URL url = new
		 * URL("http://www.sdd.com.ar/jira/rest/auth/1/session");
		 * HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		 * conn.setDoOutput(true); conn.setRequestMethod("POST");
		 * conn.setRequestProperty("Content-Type", "application/json");
		 * 
		 * String input = "{\"username\":user,\"password\":\"pass\"}";
		 * 
		 * OutputStream os = conn.getOutputStream(); os.write(input.getBytes());
		 * os.flush();
		 * 
		 * if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) { throw
		 * new RuntimeException("Failed : HTTP error code : " +
		 * conn.getResponseCode()); }
		 * 
		 * BufferedReader br = new BufferedReader(new
		 * InputStreamReader((conn.getInputStream())));
		 * 
		 * String output; System.out.println("Output from Server .... \n");
		 * while ((output = br.readLine()) != null) {
		 * System.out.println(output); }
		 * 
		 * conn.disconnect();
		 * 
		 * } catch (MalformedURLException e) { e.printStackTrace(); } catch
		 * (IOException e) { e.printStackTrace(); }
		 */
		try {
			URL url = new URL("http://www.sdd.com.ar/jira/rest/api/2.0.alpha1/issue/ARSA-1");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			String encoded = "para tener Base64, incluir commons-codec 1.6";// Base64.encodeBase64String("user:pass".getBytes());
			conn.setRequestProperty("Authorization", "Basic " + encoded);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
			conn.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void doitRPC() {
		try {
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			config.setServerURL(new URL("http://www.sdd.com.ar/jira/rpc/xmlrpc"));
			config.setEnabledForExtensions(true);
			config.setConnectionTimeout(60 * 1000);
			config.setReplyTimeout(60 * 1000);

			// Initialise RPC Client
			XmlRpcClient rpcClient = new XmlRpcClient();
			rpcClient.setConfig(config);

			System.out.print("Entre password jira: ");
			Scanner in = new Scanner(System.in);
			String password = in.next();
			in.close();
			
			String loginToken = (String) rpcClient.execute("jira1.login", Arrays.asList("eviera", password));

			HashMap<String, String> sie1 = (HashMap<String, String>) rpcClient.execute("jira1.getIssue", Arrays.asList(loginToken, "SIE-1"));
			System.out.println("Issue: " + sie1.get("key"));
			Object comps = (Object) rpcClient.execute("jira1.getComponents", Arrays.asList(loginToken, "TKT"));
			System.out.println("Comps: " + comps);
			/*
			 * List projects =
			 * (List)rpcClient.execute("jira1.getProjectsNoSchemes",
			 * loginTokenVector); for (Iterator iterator = projects.iterator();
			 * iterator.hasNext();) { Map project = (Map) iterator.next();
			 * System.out.println(project.get("name") + " with lead " +
			 * project.get("lead")); }
			 */

			Hashtable<String, Object> struct = new Hashtable<String, Object>();
			// Constants for issue creation
			struct.put("summary", "Prueba XMLRPC4");
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

			// Log out
			Boolean bool = (Boolean) rpcClient.execute("jira1.logout", Arrays.asList(loginToken));
			System.out.println("Logout successful: " + bool);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
    private static  Hashtable<String, Object> makeCustomFieldHashtable(String customFieldId, String value) {
        Hashtable<String, Object> t = new Hashtable<String, Object>();
        t.put("customfieldId", customFieldId);
        t.put("values", Arrays.asList(value));
        return t;
    }

	public static void main(String[] args) {
		TestJira.doitRPC();
	}

}
