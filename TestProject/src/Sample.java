import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Sample {
	public static void main(String[] args) {
		Connection connection = null;
		try {
			try {
				Class.forName("sdb.JDBC");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return;
			}

			// create a database connection
			connection = DriverManager.getConnection("jdbc:sdb://10.10.3.32:2639/test/sample.gdf", "sa", "sdb-admin");			
			//connection = DriverManager.getConnection("jdbc:sdb:test/sample.gdf");

			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			statement.executeUpdate("drop table if exists person");
			statement.executeUpdate("create table person (id integer, name string, v1 long)");
			statement.executeUpdate("insert into person values(1, 'leo', 1)");
			statement.executeUpdate("insert into person values(2, 'yui', 2)");
			ResultSet rs = statement.executeQuery("select * from person");

			while (rs.next()) {
				// read the result set
				System.out.println("name = " + rs.getString("name"));
				System.out.println("id = " + rs.getInt("id"));
				System.out.println("v1 = " + rs.getLong("v1"));
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e);
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				// connection close failed.
				System.err.println(e);
			}
		}
	}
}
