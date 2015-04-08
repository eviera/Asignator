package ar.com.sdd.asignator.service;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ConfigurationService {

	private final Logger log = Logger.getLogger(getClass());
	
	private static ConfigurationService instance = null;
	
	private Properties props;
	
	private ConfigurationService() {
		try {
			//Levanto las properties de la base H2
			props = getPropertiesFromH2();
		} catch (Exception e) {
			log.error("Error al levantar las properties", e);
		}
	}

	public static synchronized ConfigurationService getInstance() {
		if (instance == null) {
			instance = new ConfigurationService();
		}
		return instance;
	}

	public Map<String, String> getPropertiesMap() {
		Map<String, String> map = new HashMap<String, String>();
		for (String key : props.stringPropertyNames()) {
		    map.put(key, props.getProperty(key));
		}
		return map;
	}
	
	public String getProperty(String key) {
		return props.getProperty(key);
	}

	public void setPropertiesMap(Map<String, String> propertiesMap) {
		props = new Properties();
		for (Entry<String, String> entry : propertiesMap.entrySet()) {
			props.put(entry.getKey(), entry.getValue());
		}
		
		try {
			setPropertiesToH2(props);
		} catch (Exception e) {
			log.error("Error al guardar las properties", e);
		}
	}

	/**
	 * Levanta la base H2 del directorio del usuario
	 * Si no tiene la table ASIGNATOR_PROPERTIES la crea y popula con las properties basicas
	 * Carga las properties a memoria
	 * @return
	 */
	private Properties getPropertiesFromH2() throws Exception {
		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection("jdbc:h2:~/asignator_database");
		Statement stmt = conn.createStatement();
		//Creo la tabla si no existe
		stmt.execute("CREATE TABLE IF NOT EXISTS ASIGNATOR_PROPERTIES (ID INT PRIMARY KEY, PROPS VARCHAR(4000));");
		//Intento levantar las properties del registro con id 0. Si no hay, agrego uno
        ResultSet rs = stmt.executeQuery("select props from ASIGNATOR_PROPERTIES where id=0");
        String props = null;
        
        while (rs.next()) {
            props = rs.getString("props");
        }
        conn.close();
        
		if (props == null || props.equals("")) {
			//Inserto valores por default
			StringBuilder defaultProps = new StringBuilder();
			defaultProps.append("mail_server_host=").append("\n");
			defaultProps.append("mail_server_port=").append("\n");
			defaultProps.append("mail_server_user=").append("\n");
			defaultProps.append("mail_server_pass=").append("\n");
			defaultProps.append("mail_server_folder=").append("\n");
			defaultProps.append("mail_blacklist=").append("\n");

			defaultProps.append("jira_server_host=").append("\n");
			defaultProps.append("jira_server_user=").append("\n");
			defaultProps.append("jira_server_pass=").append("\n");
			defaultProps.append("jira_issue_assignee=").append("\n");

			props = defaultProps.toString();
			
			PreparedStatement preparedStatement = conn.prepareStatement("insert into ASIGNATOR_PROPERTIES (id, props) values (0, ?)");
			preparedStatement.setString(1, props);
			preparedStatement.executeUpdate();				
		}
        
		Properties properties = new Properties();
		properties.load(new StringReader(props));
			
		return properties;
	}

	private void setPropertiesToH2(Properties props) throws Exception {
		log.info("Guardando las properties en H2");
		StringBuilder propsString = new StringBuilder();
		for (String key : props.stringPropertyNames()) {
			propsString.append(key).append("=").append(props.getProperty(key)).append("\n");
		}

		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection("jdbc:h2:~/asignator_database");
		PreparedStatement preparedStatement = conn.prepareStatement("update ASIGNATOR_PROPERTIES set props = ? where id = 0;");
		preparedStatement.setString(1, propsString.toString());
		int modified = preparedStatement.executeUpdate();
		log.info("Se han modificado [" + modified + "] registros");
		conn.close();
	}
	
}
