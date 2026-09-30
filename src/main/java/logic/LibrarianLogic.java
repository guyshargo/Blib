package logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.MysqlConnection;
import model.Librarian;

public class LibrarianLogic {

    //Connection to the mySQL database
    private final MysqlConnection dbConnector;

    // Singleton database connector
    public LibrarianLogic() { dbConnector = MysqlConnection.getInstance(); }


    public synchronized Librarian fetchLibrarianByUsername(String username) {
        String query = "SELECT * FROM librarian_db WHERE userName = ? ";
        Librarian librarian = null;

        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch username: " + e.getMessage());
        }
        return librarian;
    }

    /**
     * Description:
     * Method for disconnecting all librarians
     */
    public synchronized void logOutStatusToAllLibrarian() {
        String query = "UPDATE librarian_db SET LoggedInStatus = ? WHERE LoggedInStatus = ?";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setBoolean(1, false);
            ps.setBoolean(2, true);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Failed to logout librarians: " + e.getMessage());
        }
    }


    public synchronized boolean ChangeLogInStatus(int librarianId, boolean status) {
        String query = "UPDATE librarian_db SET LoggedInStatus = ? WHERE LlibrarianID = ?";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setBoolean(1, status);
            ps.setInt(2, librarianId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Failed to update librarian login status: " + e.getMessage());
        }
        return false;
    }

    public synchronized Librarian fetchLibrarianByID(int librarianID) {
        String query = "SELECT * FROM librarian_db WHERE LlibrarianID = ?";
        Librarian librarian = null;
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setInt(1, librarianID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    librarian = new Librarian(
                            rs.getString("fullName"),
                            rs.getString("PhoneNumber"),
                            rs.getInt("LlibrarianID"),
                            rs.getString("emailAdress"),
                            rs.getString("userName"),
                            rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch librarian ID: " + e.getMessage());
        }
        return librarian;
    }
}