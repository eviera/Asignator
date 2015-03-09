package ar.com.sdd.asignator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//http://www.mkyong.com/webservices/jax-rs/restfull-java-client-with-java-net-url/
//Usar Gson con https://developer.atlassian.com/static/rest/jira/4.4.1.html#id150510



public class TestJira {

	public static void doit() {
		try {
			URL url = new URL("http://www.sdd.com.ar/jira/rest/api/2.0.alpha1/issue/ARSA-1");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
		TestJira.doit();
	}
	
}
