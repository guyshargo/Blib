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
        String query = "SELECT * FROM invoices";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                Subject subject = this.getSubject(rs.getString("subject"));
                IsRead is_read = this.getIsRead(rs.getString("is_read"));
                
                InvoiceMessage message = new InvoiceMessage(
                        rs.getInt("message_id"),
                        rs.getInt("member_id"),
                        rs.getString("username"),
                        rs.getString("member_name"),
                        subject,
                        rs.getString("content"),
                        rs.getDate("message_date"),
                        is_read
                );
                list.add(message);
            }
        } catch (SQLException e) {
            System.out.println("Error importing invoice messages: " + e.getMessage());
        }
        return list; // Returning an empty list on failure
    }


    public synchronized boolean sendMessage(int member_id, String username, String member_name, Subject subject, String content) {
        String query = "INSERT INTO invoices (member_id, username, member_name, subject, content, message_date, is_read) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        Date tempDate = new Date();
        java.sql.Date currentDate = new java.sql.Date(tempDate.getTime());
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setInt(1, member_id);
            ps.setString(2, username);
            ps.setString(3, member_name);
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


    public synchronized void readMessage(int message_id) {
        String query = "UPDATE invoices SET is_read = ? WHERE message_id = ?";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setString(1, "Read");
            ps.setInt(2, message_id);
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

    private synchronized IsRead getIsRead(String is_read) {
        if ("Read".equals(is_read)) {
            return IsRead.READ;
        } else {
            return IsRead.NOT_READ;
        }
    }
}