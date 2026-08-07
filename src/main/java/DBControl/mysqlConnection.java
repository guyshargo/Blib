package DBControl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class mysqlConnection {

	private static mysqlConnection instance; // singleton design pattern
	private Connection conn; // singleton design pattern

	// connection to driver and mysql
	private mysqlConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			System.out.println("Driver definition succeed");
		} catch (Exception ex) {
			/* handle the error */
			System.out.println("Driver definition failed");
		}

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/library?serverTimezone=IST", "root", "Aa123456");
			System.out.println("SQL connection succeed");
		} catch (SQLException ex) {/* handle any errors */
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}

	// singleton for DB connection
	public static mysqlConnection getInstance() {
		if (instance == null) {
			instance = new mysqlConnection();
		}
		return instance;
	}

	// get connection
	public Connection getConnection() {
		return conn;
	}

}