package DBControl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Description:
 * Class for connecting to the mySQL database
 */
public class mysqlConnection {
    /**
     * Singleton instance for the class object
     */
    private static mysqlConnection instance;

    /**
     * Singleton instance for the connection
     */
    private Connection conn;


    /**
     * Description:
     * Method for constructing the given class
     */
    private mysqlConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            System.out.println("Driver definition succeed");
        } catch (Exception ex) {
            System.out.println("Driver definition failed");
        }
        openConnection();
    }

    /**
     * Description:
     * Method for ensuring that there is only one instance of mysqlConnection
     */
    public static mysqlConnection getInstance() {
        if (instance == null) {
            instance = new mysqlConnection();
        }
        return instance;
    }

    /**
     * Description:
     * Method for opening the connection if it's not already open or is closed
     */
    private void openConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection("jdbc:mysql://localhost/blib?serverTimezone=IST", "root", "Aa123456");
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    /**
     * Description:
     * Method for retrieving the connection instance
     *
     * @return conn Connection.class
     */
    public Connection getConnection() {
        openConnection();  // Ensure connection is open
        return conn;
    }
}