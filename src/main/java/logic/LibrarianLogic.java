package logic;

import DBControl.mysqlConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Description:
 * Class for the librarian logic in the system
 */
public class LibrarianLogic {
    /**
     * Connection to the mySQL database
     */
    private mysqlConnection dbConnector;

    /**
     * Description:
     * Method for constructing the given class
     */
    public LibrarianLogic() {
        dbConnector = mysqlConnection.getInstance(); // Singleton database connector
    }

    /**
     * Description:
     * Method for fetching a librarian by username
     *
     * @param username String.class
     * @return librarian Librarian.class
     */
    public synchronized Librarian fetchLibrarianByUsername(String username) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "SELECT * FROM librarian_db WHERE userName = ? ";
        Librarian librarian = null;
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, username); // Set the librarian username in the query
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Map the database row to a librarian object
                librarian = new Librarian(
                        rs.getString("fullName"),
                        rs.getString("PhoneNumber"),
                        rs.getInt("LlibrarianID"),
                        rs.getString("emailAdress"),
                        rs.getString("userName"),
                        rs.getString("password")
                );
                librarian.setLoginStatus(rs.getBoolean("LoggedInStatus"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch username: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return librarian; // Return the Librarian
    }

    /**
     * Description:
     * Method for disconnecting all librarians
     */
    public synchronized void logOutStatusToAllLibrarian() {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "UPDATE librarian_db  SET LoggedInStatus = ?  WHERE LoggedInStatus =?";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setBoolean(1, false); // Set the login status
            ps.setBoolean(2, true); // Set the login status
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to fetch librarian: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Description:
     * Method for changing librarian's login status when they are connected
     *
     * @param librarianId int
     * @param status      boolean
     * @return boolean (successful change)
     */
    public synchronized boolean ChangeLogInStatus(int librarianId, boolean status) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "UPDATE librarian_db SET LoggedInStatus = ? WHERE LlibrarianID = ?";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setBoolean(1, status); // Set the login status
            ps.setInt(2, librarianId); // Set the membership number (id)
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0; // Return true if at least one row was updated
        } catch (SQLException e) {
            System.out.println("Failed to update librarian login status: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false; // Return false in case of failure
    }

    /**
     * Description:
     * Method for fetching librarian by username
     *
     * @param librarianID int
     * @return librarian Librarian.class
     */
    public synchronized Librarian fetchLibrarianByID(int librarianID) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "SELECT * FROM librarian_db WHERE LlibrarianID = ? ";
        Librarian librarian = null;
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setInt(1, librarianID); // Set the librarian username in the query
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Map the database row to a librarian object
                librarian = new Librarian(
                        rs.getString("fullName"),
                        rs.getString("PhoneNumber"),
                        rs.getInt("LlibrarianID"),
                        rs.getString("emailAdress"),
                        rs.getString("userName"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch username: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return librarian; // Return the Librarian
    }
}