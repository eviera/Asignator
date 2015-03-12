package ar.com.sdd.asignator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;

import org.apache.commons.codec.binary.Base64;

//http://crunchify.com/create-very-simple-jersey-rest-service-and-send-json-data-from-java-client/
//http://www.mkyong.com/webservices/jax-rs/restfull-java-client-with-java-net-url/
//Usar Gson con https://developer.atlassian.com/static/rest/jira/4.4.1.html#id150510

//con SOAP
//http://www.sdd.com.ar/jira/rpc/soap/jirasoapservice-v2?wsdl
//http://stackoverflow.com/questions/19502158/creating-a-soap-client-in-java-given-a-wsdl-file

public class TestJira {

	/*
	public static void doitJersey() {
		try {

			JerseyJiraRestClientFactory f = new JerseyJiraRestClientFactory();
			JiraRestClient jc = f.createWithBasicHttpAuthentication(new URI("http://www.sdd.com.ar/jira"), "eviera", "");

			SearchResult r = jc.getSearchClient().searchJql("type = Epic ORDER BY RANK ASC", null);

			Iterator<BasicIssue> it = r.getIssues().iterator();
			while (it.hasNext()) {

				Issue issue = jc.getIssueClient().getIssue(((BasicIssue) it.next()).getKey(), null);

				System.out.println("Epic: " + issue.getKey() + " " + issue.getSummary());

				Iterator<IssueLink> itLink = issue.getIssueLinks().iterator();
				while (itLink.hasNext()) {

					IssueLink issueLink = (IssueLink) itLink.next();
					Issue issueL = jc.getIssueClient().getIssue((issueLink).getTargetIssueKey(), null);

					System.out.println(issueLink.getIssueLinkType().getDescription() + ": " + issueL.getKey() + " " + issueL.getSummary() + " "
							+ issueL.getFieldByName("Story Points").getValue());

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	*/

	public static void doitHTTP() {
		// Login
		/*
		try {
			URL url = new URL("http://www.sdd.com.ar/jira/rest/auth/1/session");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			String input = "{\"username\":eviera,\"password\":\"sasa\"}";

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}

			conn.disconnect();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		try {
			URL url = new URL("http://www.sdd.com.ar/jira/rest/api/2.0.alpha1/issue/ARSA-1");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			String encoded = Base64.encodeBase64String("eviera:telperion".getBytes());
			conn.setRequestProperty("Authorization", "Basic "+encoded);
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

	public static void main(String[] args) {
		TestJira.doitHTTP();
	}

}
