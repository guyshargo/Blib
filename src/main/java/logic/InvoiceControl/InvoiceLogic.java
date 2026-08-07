package logic.InvoiceControl;

import DBControl.mysqlConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description:
 * Class for the messaging logic of the librarian's invoice in the system
 */
public class InvoiceLogic {
    /**
     * Connection to the mySQL database
     */
    private mysqlConnection dbConnector;

    /**
     * Description:
     * Method for constructing the given class
     */
    public InvoiceLogic() {
        this.dbConnector = mysqlConnection.getInstance(); // Singleton database connector
    }

    /**
     * Description:
     * Method for importing all messages to invoice of the librarian
     *
     * @return list List<InvoiceMessage>.class
     */
    public synchronized List<InvoiceMessage> importMessages() {
        Connection connection = null;
        PreparedStatement ps = null;
        // Initialize list of all messages
        List<InvoiceMessage> list = new ArrayList<>();
        // String to retrieve all messages
        String query = "SELECT * FROM invoice_db";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Subject subject = this.getSubject(rs.getString("subject"));
                IsRead isRead = this.getIsRead(rs.getString("isRead"));
                InvoiceMessage message = new InvoiceMessage(
                        rs.getInt("messageID"),
                        rs.getInt("membershipNumber"),
                        rs.getString("username"),
                        rs.getString("name"),
                        subject,
                        rs.getString("content"),
                        rs.getDate("messageDate"),
                        isRead
                );
                list.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        return list;
    }

    /**
     * Description:
     * Method for adding an InvoiceMessage
     *
     * @param membershipNumber int
     * @param username         String.class
     * @param name             String.class
     * @param subject          Subject.class
     * @param content          String.class
     * @return boolean (true if changed in the database)
     */
    public synchronized boolean sendMessage(int membershipNumber, String username, String name, Subject subject,
                                            String content) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "INSERT INTO invoice_db (membershipNumber, username, name, subject, content, messageDate, isRead) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Date tempDate = new Date();
        java.sql.Date currentDate = new java.sql.Date(tempDate.getTime());
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setInt(1, membershipNumber);
            ps.setString(2, username);
            ps.setString(3, name);
            ps.setString(4, subject.toString());
            ps.setString(5, content);
            ps.setDate(6, currentDate);
            ps.setString(7, "notRead");
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * Description:
     * Method for changing Read status
     *
     * @param messageID int
     */
    public synchronized void readMessage(int messageID) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "UPDATE invoice_db SET isRead = ? WHERE messageID = ?";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, "Read");
            ps.setInt(2, messageID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    /**
     * Description:
     * Getter method for the invoice message's subject
     *
     * @param subject String.class
     * @return Subject ('Extension', 'General')
     */
    private synchronized Subject getSubject(String subject) {
        if (subject.equals("Extension"))
            return Subject.EXTENSION;
        else
            return Subject.GENERAL_MESSAGE;
    }

    /**
     * Description:
     * Getter method for the invoice message's subject
     *
     * @param isRead String.class
     * @return IsRead ('Read', 'notRead')
     */
    private synchronized IsRead getIsRead(String isRead) {
        if (isRead.equals("Read"))
            return IsRead.READ;
        else
            return IsRead.NOT_READ;
    }
}