package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Description:
 * Class for connecting to the mySQL database
 */
public class MysqlConnection {

    private static MysqlConnection instance;
    private Connection conn;
    private Properties envProperties;


    private MysqlConnection() {
        envProperties = new Properties();

        try (FileInputStream fis = new FileInputStream(".env")) {
            envProperties.load(fis);
        } catch (IOException ex) {
            System.out.println("Warning: Could not load .env file. Falling back to default credentials.");
        }
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
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
    public static MysqlConnection getInstance() {
        if (instance == null) {
            instance = new MysqlConnection();
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
                String url = envProperties.getProperty("DB_URL", "jdbc:mysql://localhost:3306/blib?serverTimezone=Asia/Jerusalem");
                String user = envProperties.getProperty("DB_USER", "root");
                String password = envProperties.getProperty("DB_PASSWORD", "Aa123456");
                
                conn = DriverManager.getConnection(url, user, password);
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