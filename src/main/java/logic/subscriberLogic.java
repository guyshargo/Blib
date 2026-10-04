package logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import database.MysqlConnection;
import enums.FreezeStatus;
import model.Subscriber;

public class SubscriberLogic {

    private final MysqlConnection dbConnector;

    // Singleton database connector
    public SubscriberLogic() { dbConnector = MysqlConnection.getInstance(); }
    

    public synchronized boolean setFreezeStatus(int id, String freezeStatus, LocalDate freezeDate) {
        String query = "UPDATE members SET freeze_status = ?, freeze_date = ? WHERE member_id = ?";
        
        Subscriber subscriber = this.fetchSubscriberById(id);
        if (subscriber != null && subscriber.getFreezeStatus().getDbValue().equals(freezeStatus)) {
            return false;
        }
            
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setString(1, freezeStatus);
            if (freezeDate == null) {
                ps.setNull(2, java.sql.Types.DATE);
            } else {
                ps.setDate(2, java.sql.Date.valueOf(freezeDate));
            }
            ps.setInt(3, id);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public synchronized boolean updateSubscriberContact(int id, String phoneNumber, String email) {
        String query = "UPDATE members SET phone_number = ?, email_address = ? WHERE member_id = ?";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setString(1, phoneNumber);
            ps.setString(2, email);
            ps.setInt(3, id);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to update subscriber contact: " + e.getMessage());
        }
        return false;
    }


    public synchronized Subscriber fetchMemberByUsername(String username) {
        String query = "SELECT * FROM members WHERE username = ?";
        Subscriber subscriber = null;
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    subscriber = new Subscriber(
                            rs.getInt("member_id"),
                            rs.getString("full_name"),
                            rs.getString("username"),
                            rs.getString("password"),
                            FreezeStatus.fromDbValue(rs.getString("freeze_status")),
                            rs.getString("email_address"),
                            rs.getString("phone_number"),
                            rs.getString("reader_card_barcode")
                    );
                    subscriber.setLoginStatus(rs.getBoolean("is_logged_in"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch username: " + e.getMessage());
        }
        return subscriber;
    }

    
    public synchronized boolean ChangeLogInStatus(int memberID, boolean status) {
        String query = "UPDATE members SET is_logged_in = ? WHERE member_id = ?";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setBoolean(1, status);
            ps.setInt(2, memberID);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to update subscriber login status: " + e.getMessage());
        }
        return false;
    }


    public synchronized Subscriber fetchSubscriberById(int subscriberId) {
        String query = "SELECT * FROM members WHERE member_id = ?";
        Subscriber subscriber = null;
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setInt(1, subscriberId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    subscriber = new Subscriber(
                            rs.getInt("member_id"),
                            rs.getString("full_name"),
                            rs.getString("username"),
                            rs.getString("password"),
                            FreezeStatus.fromDbValue(rs.getString("freeze_status")),
                            rs.getString("email_address"),
                            rs.getString("phone_number"),
                            rs.getString("reader_card_barcode")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch subscriber: " + e.getMessage());
        }
        return subscriber;
    }


    public synchronized String checkDuplicates(int membershipNumber, String username, String email) {
        StringBuilder errors = new StringBuilder();

        if (isIDTaken(membershipNumber)) {
            errors.append("-Id already exist in the system please enter different id\n");
        }
        if (isUsernameTaken(username)) {
            errors.append("-userName is already taken please enter a different username\n");
        }
        if (isEmailTaken(email)) {
            errors.append("-Email already exist in the system please enter different email address");
        }

        return errors.length() > 0 ? errors.toString() : null;
    }

    
    public synchronized boolean isUsernameTaken(String username) {
        String query = "SELECT * FROM members WHERE username = ?";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error checking username: " + e.getMessage());
        }
        return false;
    }

    
    public synchronized boolean isEmailTaken(String email) {
        String query = "SELECT * FROM members WHERE email_address = ?";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error checking email: " + e.getMessage());
        }
        return false;
    }

    
    public synchronized boolean isIDTaken(int memberid) {
        String query = "SELECT * FROM members WHERE member_id = ?";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setInt(1, memberid);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error checking ID: " + e.getMessage());
        }
        return false;
    }

    
    public synchronized List<Subscriber> fetchAllSubscribers() {
        String query = "SELECT * FROM members";
        List<Subscriber> subscribers = new ArrayList<>();
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                Subscriber sub = new Subscriber(
                        rs.getInt("member_id"),
                        rs.getString("full_name"),
                        rs.getString("username"),
                        rs.getString("password"),
                        FreezeStatus.fromDbValue(rs.getString("freeze_status")),
                        rs.getString("email_address"),
                        rs.getString("phone_number"),
                        rs.getString("reader_card_barcode"));
                        
                // Fetch and set the freeze status date if not null
                LocalDate freezeStatusDate = rs.getDate("freeze_date") == null ? null : rs.getDate("freeze_date").toLocalDate();
                sub.setFreezeStatusDate(freezeStatusDate);
                subscribers.add(sub);
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch subscribers: " + e.getMessage());
        }
        return subscribers;
    }

    
    public synchronized void logOutStatusToAllSubscribers() {
        String query = "UPDATE members SET is_logged_in = ? WHERE is_logged_in = ?";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setBoolean(1, false);
            ps.setBoolean(2, true);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to logout subscribers: " + e.getMessage());
        }
    }

    
    public synchronized List<Subscriber> fetchFrozenSubscribersOlderThanAMonth() {
        String query = "SELECT * FROM members WHERE freeze_status = ? AND freeze_date = ?";
        List<Subscriber> subscribersToUnfreeze = new ArrayList<>();
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
            ps.setString(1, FreezeStatus.FROZEN.getDbValue());
            ps.setDate(2, java.sql.Date.valueOf(oneMonthAgo));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    subscribersToUnfreeze.add(new Subscriber(
                            rs.getInt("membershipNumber"),
                            rs.getString("MemberFullName"),
                            rs.getString("userName"),
                            rs.getString("password"),
                            FreezeStatus.fromDbValue(rs.getString("memberFreezeStatus")),
                            rs.getString("emailAddress"),
                            rs.getString("memberPhoneNumber"),
                            rs.getDate("memberFreezeDate").toLocalDate(),
                            rs.getString("readerCardBarcode")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch subscribers to unfreeze: " + e.getMessage());
        }
        return subscribersToUnfreeze;
    }

    
    public synchronized Subscriber fetchSubscriberScanBarcode(String barcode) {
        String query = "SELECT * FROM members WHERE reader_card_barcode = ?";
        Subscriber subscriber = null;
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setString(1, barcode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    subscriber = new Subscriber(
                            rs.getInt("member_id"),
                            rs.getString("full_name"),
                            rs.getString("username"),
                            rs.getString("password"),
                            FreezeStatus.fromDbValue(rs.getString("freeze_status")),
                            rs.getString("email_address"),
                            rs.getString("phone_number"),
                            rs.getString("reader_card_barcode")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch subscriber by barcode: " + e.getMessage());
        }
        return subscriber;
    }

    
    public synchronized Subscriber registerNewMember(int membershipNumber, String fullName, String username,
                                                     String password, FreezeStatus freezeStatus, String phone, String email) {
        if (isUsernameTaken(username)) {
            return null;
        }

        String barcode = customHashBarcode(membershipNumber, fullName);
        String query = "INSERT INTO members (member_id, full_name, username, password, freeze_status, email_address, phone_number, reader_card_barcode) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setInt(1, membershipNumber);
            ps.setString(2, fullName);
            ps.setString(3, username);
            ps.setString(4, password);
            ps.setString(5, freezeStatus.getDbValue());
            ps.setString(6, email);
            ps.setString(7, phone);
            ps.setString(8, barcode);
            
            if (ps.executeUpdate() > 0) {
                return new Subscriber(membershipNumber, fullName, username, password, freezeStatus, email, phone, barcode);
            }
        } catch (SQLException e) {
            System.out.println("Error during registration: " + e.getMessage());
        }
        return null;
    }

    public synchronized static String customHashBarcode(int membershipNumber, String memberFullName) {
        String rawData = membershipNumber + "|" + memberFullName;
        return Base64.getEncoder().encodeToString(rawData.getBytes());
    }
}