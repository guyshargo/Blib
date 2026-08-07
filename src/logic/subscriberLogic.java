package logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import DBControl.mysqlConnection;

/**
 * Description:
 * Class for the subscriber logic in the system
 */
public class subscriberLogic {
    /**
     * Connection to the mySQL database
     */
    private mysqlConnection dbConnector;

    /**
     * Description:
     * Method for constructing the given class
     */
    public subscriberLogic() {
        dbConnector = mysqlConnection.getInstance(); // Singleton database connector
    }

    /**
     * Description:
     * Method for freezing a given subscriber
     *
     * @param id           int
     * @param freezeStatus String.class
     * @param freezeDate   LocalDate.class
     * @return boolean (successful change)
     */
    public synchronized boolean setFreezeStatus(int id, String freezeStatus, LocalDate freezeDate) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "UPDATE member_db SET memberFreezeStatus = ?, memberFreezeDate = ? WHERE membershipNumber = ?";
        Subscriber subscriber = this.fetchSubscriberById(id);
        if (subscriber.getMemberFreezeStatus().getDbValue().equals(freezeStatus))
            return false;
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, freezeStatus); // Set the freeze status as a String ("Frozen" or "NotFrozen")
            if (freezeDate == null) {
                ps.setNull(2, java.sql.Types.DATE); // Set freezeDate to NULL
            } else {
                ps.setDate(2, java.sql.Date.valueOf(freezeDate)); // Set the actual date
            }
            ps.setInt(3, id); // Set the membership number (id)
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0; // Return true if at least one row was updated
        } catch (SQLException e) {
            e.printStackTrace();
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
     * Method for updating subscriber's contact information
     *
     * @param id          int
     * @param phoneNumber String.class
     * @param email       String.class
     * @return boolean (successful change)
     */
    public synchronized boolean updateSubscriberContact(int id, String phoneNumber, String email) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "UPDATE member_db SET memberPhoneNumber = ?, emailAddress = ? WHERE membershipNumber = ?";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, phoneNumber);
            ps.setString(2, email);
            ps.setInt(3, id);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Failed to update subscriber contact: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Description:
     * Method for retrieving a member by given username from the member_db
     *
     * @param username String.class
     * @return subscriber Subscriber.class
     */
    public synchronized Subscriber fetchMemberByUsername(String username) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "SELECT * FROM member_db WHERE userName = ?";
        Subscriber subscriber = null;
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, username); // Set the subscriber ID in the query
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Map the database row to a Subscriber object
                subscriber = new Subscriber(
                        rs.getInt("membershipNumber"),
                        rs.getString("MemberFullName"),
                        rs.getString("userName"),
                        rs.getString("password"),
                        FreezeStatus.fromDbValue(rs.getString("memberFreezeStatus")),
                        rs.getString("emailAddress"),
                        rs.getString("memberPhoneNumber"),
                        rs.getString("readerCardBarcode")
                );
                subscriber.setLoginStatus(rs.getBoolean("LoggedInStatus"));
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
        return subscriber; // Return the subscriber
    }

    /**
     * Description:
     * Method for changing Subscriber's login status when they are connected
     *
     * @param memberID int
     * @param status   boolean
     * @return boolean (successful change)
     */
    public synchronized boolean ChangeLogInStatus(int memberID, boolean status) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "UPDATE member_db SET LoggedInStatus = ? WHERE membershipNumber = ?";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setBoolean(1, status); // Set the login status
            ps.setInt(2, memberID); // Set the membership number (id)
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0; // Return true if at least one row was updated
        } catch (SQLException e) {
            System.out.println("Failed to update subscriber login status: " + e.getMessage());
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
     * Method for fetching subscriber information via their membership number
     *
     * @param subscriberId int
     * @return subscriber Subscriber.class
     */
    public synchronized Subscriber fetchSubscriberById(int subscriberId) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "SELECT * FROM member_db WHERE membershipNumber = ?";
        Subscriber subscriber = null;
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setInt(1, subscriberId); // Set the subscriber ID in the query
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Map the database row to a Subscriber object
                subscriber = new Subscriber(
                        rs.getInt("membershipNumber"),
                        rs.getString("MemberFullName"),
                        rs.getString("userName"),
                        rs.getString("password"),
                        FreezeStatus.fromDbValue(rs.getString("memberFreezeStatus")),
                        rs.getString("emailAddress"),
                        rs.getString("memberPhoneNumber"),
                        rs.getString("readerCardBarcode")
                );
                return subscriber;
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch subscriber: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Description:
     * Method for checking if in the member_db there is a member with the same membershipNumber, username or email
     *
     * @param membershipNumber int
     * @param username         String.class
     * @param email            String.class
     * @return returnMsg Message.class
     */
    public synchronized Message checkDuplicates(int membershipNumber, String username, String email) {
        Message returnmsg = new Message("RegistrationSuccess", null);
        String data = "";
        if (isIDTaken(membershipNumber)) {
            returnmsg.setCommand("RegistrationFailed");
            data += "-Id already exist in the system please enter different id\n";
        }
        if (isUsernameTaken(username)) {
            returnmsg.setCommand("RegistrationFailed");
            data += "-userName is already taken please enter a different username\n";
        }
        if (isEmailTaken(email)) {
            returnmsg.setCommand("RegistrationFailed");
            data += "-Email already exist in the system please enter different email address";
        }
        returnmsg.setData(data);
        return returnmsg;
    }

    /**
     * Description:
     * Method for checking if given username has been taken by another member in the member_db
     *
     * @param username String.class
     * @return boolean (successful change)
     */
    public synchronized boolean isUsernameTaken(String username) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "SELECT * FROM member_db WHERE userName = ?";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;  // Username exists
            }
        } catch (SQLException e) {
            System.out.println("Error checking username: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;  // Username does not exist
    }

    /**
     * Description:
     * Method for checking if given email has been taken by another member in the member_db
     *
     * @param email String.class
     * @return boolean (successful change)
     */
    public synchronized boolean isEmailTaken(String email) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "SELECT * FROM member_db WHERE emailAddress = ?";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;  // Username exists
            }
        } catch (SQLException e) {
            System.out.println("Error checking username: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;  // Username does not exist
    }

    /**
     * Description:
     * Method for checking if given member's id has been taken by another member in the member_db
     *
     * @param memberid int
     * @return boolean (successful change)
     */
    public synchronized boolean isIDTaken(int memberid) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "SELECT * FROM member_db WHERE membershipNumber = ?";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setInt(1, memberid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;  // Username exists
            }
        } catch (SQLException e) {
            System.out.println("Error checking username: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;  // Username does not exist
    }

    /**
     * Description:
     * Method for retrieving all subscribers from the member_db
     *
     * @return subscribers List<Subscriber>.class
     */
    public synchronized List<Subscriber> fetchAllSubscribers() {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "SELECT * FROM member_db";
        List<Subscriber> subscribers = new ArrayList<>();
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Subscriber sub = new Subscriber(
                        rs.getInt("membershipNumber"),
                        rs.getString("MemberFullName"),
                        rs.getString("userName"),
                        rs.getString("password"),
                        FreezeStatus.fromDbValue(rs.getString("memberFreezeStatus")),
                        rs.getString("emailAddress"),
                        rs.getString("memberPhoneNumber"),
                        rs.getString("readerCardBarcode"));
                // Fetch and set the freeze status date if not null
                LocalDate freezeStatusDate = rs.getDate("memberFreezeDate") == null ? null : rs.getDate("memberFreezeDate").toLocalDate();
                sub.setFreezeStatusDate(freezeStatusDate);
                subscribers.add(sub); // Add subscriber to the list
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch subscribers: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return subscribers; // Return the subscriber list
    }

    /**
     * Description:
     * Method for logging out all subscribers from the system
     */
    public synchronized void logOutStatusToAllSubscribers() {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "UPDATE member_db  SET LoggedInStatus = ?  WHERE LoggedInStatus =?";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setBoolean(1, false); // Set the login status
            ps.setBoolean(2, true); // Set the login status
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to fetch subscribers: " + e.getMessage());
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
     * Method fetching subscribers with frozen status and freeze date older than a month
     *
     * @return subscribersToUnfreeze List<Subscriber>.class
     */
    public synchronized List<Subscriber> fetchFrozenSubscribersOlderThanAMonth() {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "SELECT * FROM member_db WHERE memberFreezeStatus = ? AND memberFreezeDate = ?";
        List<Subscriber> subscribersToUnfreeze = new ArrayList<>();
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            // Calculate the date exactly one month before today
            LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
            ps.setString(1, FreezeStatus.FROZEN.getDbValue()); // Assuming "FROZEN" is the correct status value
            ps.setDate(2, java.sql.Date.valueOf(oneMonthAgo)); // Bind the calculated date
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Map the database row to a Subscriber object
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
        } catch (SQLException e) {
            System.out.println("Failed to fetch subscribers to unfreeze: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return subscribersToUnfreeze; // Return the list of subscribers to unfreeze
    }

    /**
     * Description:
     * Method for fetching a subscriber based on given barcode
     *
     * @param barcode String.class
     * @return subscriber Subscriber.class
     */
    public synchronized Subscriber fetchSubscriberScanBarcode(String barcode) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "SELECT * FROM member_db WHERE readerCardBarcode = ?";
        Subscriber subscriber = null;
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, barcode); // Set the subscriber ID in the query
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Map the database row to a Subscriber object
                subscriber = new Subscriber(
                        rs.getInt("membershipNumber"),
                        rs.getString("MemberFullName"),
                        rs.getString("userName"),
                        rs.getString("password"),
                        FreezeStatus.fromDbValue(rs.getString("memberFreezeStatus")),
                        rs.getString("emailAddress"),
                        rs.getString("memberPhoneNumber"),
                        rs.getString("readerCardBarcode")
                );
                return subscriber;
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch subscriber: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null; // Return the subscriber
    }

    /**
     * Description:
     * Method to register a new member in the database
     *
     * @param membershipNumber int
     * @param fullName         String.class
     * @param username         String.class
     * @param password         String.class
     * @param freezeStatus     FreezeStatus.class
     * @param phone            String.class
     * @param email            String.class
     * @return subscriber Subscriber.class
     */
    public synchronized Subscriber registerNewMember(int membershipNumber, String fullName, String username,
                                                     String password, FreezeStatus freezeStatus, String phone, String email) {
        Subscriber subscriber = null;
        Connection connection = null;
        PreparedStatement ps = null;
        String barcode = customHashBarcode(membershipNumber, fullName);
        String query = "INSERT INTO member_db (membershipNumber, MemberFullName, userName, password, memberFreezeStatus, emailAddress, memberPhoneNumber,readerCardBarcode) VALUES (?, ?, ?, ?, ?, ?, ?,?)";
        // Check if username is already taken
        if (isUsernameTaken(username)) {
            return null;  // Username is taken, return false
        }
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setInt(1, membershipNumber);
            ps.setString(2, fullName);
            ps.setString(3, username);
            ps.setString(4, password);
            ps.setString(5, freezeStatus.getDbValue()); // Use dbValue for enum
            ps.setString(6, email);
            ps.setString(7, phone);
            ps.setString(8, barcode);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                subscriber = new Subscriber(membershipNumber, fullName, username, password, freezeStatus, email, phone, barcode);
            }
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error during registration: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return subscriber;
    }

    /**
     * Description:
     * Method for generating a random custom barcode for each member
     *
     * @param membershipNumber int
     * @return String.class (hashcode used as a barcode)
     */
    public synchronized static String customHashBarcode(int membershipNumber, String memberFullName) {
        String rawData = membershipNumber + "|" + memberFullName;
        return Base64.getEncoder().encodeToString(rawData.getBytes());
    }
}