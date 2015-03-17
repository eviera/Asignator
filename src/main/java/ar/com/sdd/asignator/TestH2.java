package ar.com.sdd.asignator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestH2 {

	public static void main(String[] args) throws Exception {
		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection("jdbc:h2:~/testh2_BORRAR");
		Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select props from prueba where id=1");
        while (rs.next()) {
            String props = rs.getString("props");
            System.out.println(props);
        }
		conn.close();
	}
}
