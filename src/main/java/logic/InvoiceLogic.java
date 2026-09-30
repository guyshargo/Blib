package logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import database.MysqlConnection;
import enums.IsRead;
import enums.Subject;
import model.InvoiceMessage;


public class InvoiceLogic {

    private final MysqlConnection dbConnector;

    // Singleton database connector
    public InvoiceLogic() { dbConnector = MysqlConnection.getInstance(); }

    public synchronized List<InvoiceMessage> importMessages() {
        List<InvoiceMessage> list = new ArrayList<>();
        String query = "SELECT * FROM invoice_db";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
             
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
            System.out.println("Error importing invoice messages: " + e.getMessage());
        }
        return list; // Returning an empty list on failure
    }


    public synchronized boolean sendMessage(int membershipNumber, String username, String name, Subject subject, String content) {
        String query = "INSERT INTO invoice_db (membershipNumber, username, name, subject, content, messageDate, isRead) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        Date tempDate = new Date();
        java.sql.Date currentDate = new java.sql.Date(tempDate.getTime());
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setInt(1, membershipNumber);
            ps.setString(2, username);
            ps.setString(3, name);
            ps.setString(4, subject.toString());
            ps.setString(5, content);
            ps.setDate(6, currentDate);
            ps.setString(7, "notRead");
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Error sending invoice message: " + e.getMessage());
            return false;
        }
    }


    public synchronized void readMessage(int messageID) {
        String query = "UPDATE invoice_db SET isRead = ? WHERE messageID = ?";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setString(1, "Read");
            ps.setInt(2, messageID);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Error updating read status: " + e.getMessage());
        }
    }

    private synchronized Subject getSubject(String subject) {
        if ("Extension".equals(subject)) {
            return Subject.EXTENSION;
        } else {
            return Subject.GENERAL_MESSAGE;
        }
    }

    private synchronized IsRead getIsRead(String isRead) {
        if ("Read".equals(isRead)) {
            return IsRead.READ;
        } else {
            return IsRead.NOT_READ;
        }
    }
}