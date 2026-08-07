package logic.ActivityControl;

import DBControl.mysqlConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Class for the activities that have been done in the system
 */
public class activityLogic {
    /**
     * Connection to the mySQL database
     */
    private mysqlConnection dbConnector;

    /**
     * Description:
     * Method for constructing the given class
     */
    public activityLogic() {
        dbConnector = mysqlConnection.getInstance(); // Singleton database connector
    }

    /**
     * Description:
     * Method for inserting an activity that has been done in the system to the database
     *
     * @param activity Activity.class
     * @return boolean (true if changed in the database)
     */
    public synchronized boolean addActivity(Activity activity) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        String query = "INSERT INTO activity_db (membershipNumber, activityName, activityDetails, activityDate) VALUES (?, ?, ?, ?)";
        try {
            connection = dbConnector.getConnection();
            pstmt = connection.prepareStatement(query);

            // Set parameters for the prepared statement
            pstmt.setInt(1, activity.getMembershipNumber());
            pstmt.setString(2, activity.getActivityType().getDBValue());
            pstmt.setString(3, activity.getActivityDescription());
            pstmt.setTimestamp(4, Timestamp.valueOf(activity.getActivityDateTime()));

            // Execute the insert query
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Failed to add activity: " + e.getMessage());
            return false;
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Description:
     * Method for fetching all activities done from the member based on given member's id
     *
     * @param subscriberId int
     * @return activityList List<Activity>.class
     */
    public synchronized List<Activity> fetchAllActivitiesForASubscriber(int subscriberId) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "SELECT * FROM activity_db WHERE membershipNumber = ? ORDER BY activityDate ASC";
        List<Activity> activityList = new ArrayList<>();
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setInt(1, subscriberId); // Set the subscriber ID in the query
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ActivityType activityType = this.generateActivityType(rs.getString("activityName"));

                // Map the database row to an Activity object
                Activity currActivity = new Activity(
                        rs.getInt("membershipNumber"),
                        activityType,
                        rs.getString("activityDetails"),
                        rs.getTimestamp("activityDate").toLocalDateTime()
                );
                activityList.add(currActivity);
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch activities: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return activityList;
    }

    /**
     * Description:
     * Method for returning an activities type by given string value
     *
     * @param value String.class
     * @return activityType ActivityType.class
     */
    public ActivityType generateActivityType(String value) {
        ActivityType activityType;
        switch (value) {
            // Case registration of a new member
            case "registerMember":
                activityType = ActivityType.REGISTRATION;
                break;
            // Case borrowing a book
            case "borrow":
                activityType = ActivityType.BORROW;
                break;
            // Case returning a borrowed book
            case "returning":
                activityType = ActivityType.RETURN;
                break;
            // Case freeze status changed
            case "freezeStatus":
                activityType = ActivityType.FREEZE_STATUS;
                break;
            // Case extending a borrowed book
            case "extendBorrow":
                activityType = ActivityType.EXTEND;
                break;
            // Case ordering book
            case "order":
                activityType = ActivityType.ORDER;
                break;
            // Case canceling an order
            case "cancelOrder":
                activityType = ActivityType.CANCEL_ORDER;
                break;
            // Case late return
            case "lateBookReturn":
                activityType = ActivityType.LATE_BOOK_RETURN;
                break;
            default:
                activityType = ActivityType.DEFAULT;
        }
        return activityType;
    }
}