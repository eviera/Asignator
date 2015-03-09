package ar.com.sdd.asignator.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;

@Path("/configuration")
public class ConfigurationService {

	private final Logger log = Logger.getLogger(getClass());
	
	private static ConfigurationService instance = null;
	
	private Properties props;
	private String mailServerHost;
	private Integer mailServerPort;
	private String mailServerUser;
	private String mailServerPassword;
	private String mailServerFolder;
	
	private ConfigurationService() {
		//Levanto las properties
		InputStream propStream = ConfigurationService.class.getResourceAsStream("/asignator.properties");
		props = new Properties();
		try {
			props.load(propStream);
			setMailServerHost(props.getProperty("mail.server.host"));
			setMailServerPort(props.getProperty("mail.server.port") != null ? Integer.valueOf(props.getProperty("mail.server.port")) : null);
			setMailServerUser(props.getProperty("mail.server.user"));
			setMailServerPassword(props.getProperty("mail.server.password"));
			setMailServerFolder(props.getProperty("mail.server.folder"));
			
		} catch (IOException e) {
			log.error("Error al levantar las properties", e);
		}
	}
	
	public static ConfigurationService getInstance() {
		if (instance == null) {
			instance = new ConfigurationService();
		}
		return instance;
	}

	public String getMailServerHost() {
		return mailServerHost;
	}

	public void setMailServerHost(String mailServerHost) {
		this.mailServerHost = mailServerHost;
	}

	public Integer getMailServerPort() {
		return mailServerPort;
	}

	public void setMailServerPort(Integer mailServerPort) {
		this.mailServerPort = mailServerPort;
	}

	public String getMailServerUser() {
		return mailServerUser;
	}

	public void setMailServerUser(String mailServerUser) {
		this.mailServerUser = mailServerUser;
	}

	public String getMailServerPassword() {
		return mailServerPassword;
	}

	public void setMailServerPassword(String mailServerPassword) {
		this.mailServerPassword = mailServerPassword;
	}

	public String getMailServerFolder() {
		return mailServerFolder;
	}

	public void setMailServerFolder(String mailServerFolder) {
		this.mailServerFolder = mailServerFolder;
	}

	
	@GET
	@Produces("application/json")
	public String getProperty(String key) {
		return "caca";
	}
	
	@Override
	public String toString() {
		return "ConfigurationService [" + (mailServerHost != null ? "mailServerHost=" + mailServerHost + ", " : "")
				+ (mailServerPort != null ? "mailServerPort=" + mailServerPort + ", " : "")
				+ (mailServerUser != null ? "mailServerUser=" + mailServerUser + ", " : "")
				+ (mailServerPassword != null ? "mailServerPassword=" + mailServerPassword + ", " : "")
				+ (mailServerFolder != null ? "mailServerFolder=" + mailServerFolder : "") + "]";
	}

}
