import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Sample {
	public static void main(String[] args) {
		try {
			Class.forName("sdb.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}

		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:sdb://10.10.3.32/test/sample.gdf", "sa", "sdb-admin");
			// connection = DriverManager.getConnection("jdbc:sdb:test/sample.gdf");
			
			connection.setClientInfo("sdb-client-info", "TestApplication");
			//connection.setClientInfo("ApplicationName", "ApplicationName:TestApplication");
			//connection.setAutoCommit(false);
			System.out.println("AutoCommit: " + connection.getAutoCommit());

			Statement statement = connection.createStatement();
			try {
				// statement.setQueryTimeout(30); // set timeout to 30 sec.

				statement.executeUpdate("drop table if exists person");
				statement.executeUpdate("create table person (id integer, name string, v1 long, v2 double)");
				statement.executeUpdate("insert into person values(1, 'leo', 1, 1)");
				statement.executeUpdate("insert into person values(2, 'yui', 2, 2)");

				ResultSet rs = statement.executeQuery("select * from person");
				try {
					while (rs.next()) {
						System.out.println("name = " + rs.getString("name"));
						System.out.println("id = " + rs.getInt("id"));
						System.out.println("v1 = " + rs.getLong("v1"));
						System.out.println("v2 = " + rs.getDouble("v1"));
					}
				} finally {
					if (rs != null) {
						try {
							rs.close();
						} catch (SQLException e) {
							// rs close failed.
							System.err.println(e);
						}
					}
				}
			} finally {
				if (statement != null) {
					try {
						statement.close();
					} catch (SQLException e) {
						// statement close failed.
						System.err.println(e);
					}
				}
			}
		} catch (SQLException e) {
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
