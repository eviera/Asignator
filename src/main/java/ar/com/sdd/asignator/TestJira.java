package ar.com.sdd.asignator;

import java.net.URI;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;

public class TestJira {

	public static void doit() {
		try {
	        final JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory();
	        final URI jiraServerUri = new URI("http://www.sdd.com.ar/jira");
	        final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, "eviera", "");
	        final NullProgressMonitor pm = new NullProgressMonitor();
	        final Issue issue = restClient.getIssueClient().getIssue("ARSA-1", pm);
	 
	        System.out.println(issue);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public static void main(String[] args) {
		TestJira.doit();
	}
	
}
