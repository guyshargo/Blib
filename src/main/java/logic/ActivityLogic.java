package logic;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import database.MysqlConnection;
import enums.ActivityType;
import model.Activity;

public class ActivityLogic {

    private final MysqlConnection dbConnector;

    // Singleton database connector
    public ActivityLogic() { dbConnector = MysqlConnection.getInstance(); }

    public synchronized boolean addActivity(Activity activity) {
        String query = "INSERT INTO activities (member_id, activity_name, entity_id, activity_date) VALUES (?, ?, ?, ?)";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            // Set parameters for the prepared statement
            pstmt.setInt(1, activity.getMembershipNumber());
            pstmt.setString(2, activity.getActivityType().getDBValue());
            pstmt.setInt(3, activity.getEntityId());
            pstmt.setTimestamp(4, Timestamp.valueOf(activity.getActivityDateTime()));

            // Execute the insert query
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Failed to add activity: " + e.getMessage());
            return false;
        }
    }

    public synchronized List<Activity> fetchAllActivitiesForASubscriber(int subscriberId) {
        String query = "SELECT * FROM activities WHERE member_id = ? ORDER BY activity_date ASC";
        List<Activity> activityList = new ArrayList<>();
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setInt(1, subscriberId); // Set the subscriber ID in the query
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ActivityType activityType = this.generateActivityType(rs.getString("activity_name"));

                    // Map the database row to an Activity object
                    Activity currActivity = new Activity(
                            rs.getInt("member_id"),
                            activityType,
                            rs.getInt("entity_id"),
                            rs.getTimestamp("activity_date").toLocalDateTime()
                    );
                    activityList.add(currActivity);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch activities: " + e.getMessage());
        }
        return activityList;
    }

    public ActivityType generateActivityType(String value) {
        ActivityType activityType;
        switch (value) {
            case "registerMember":
                activityType = ActivityType.REGISTRATION;
                break;
            case "borrow":
                activityType = ActivityType.BORROW;
                break;
            case "returning":
                activityType = ActivityType.RETURN;
                break;
            case "freezeStatus":
                activityType = ActivityType.FREEZE_STATUS;
                break;
            case "extendBorrow":
                activityType = ActivityType.EXTEND;
                break;
            case "order":
                activityType = ActivityType.ORDER;
                break;
            case "cancelOrder":
                activityType = ActivityType.CANCEL_ORDER;
                break;
            case "lateBookReturn":
                activityType = ActivityType.LATE_BOOK_RETURN;
                break;
            default:
                activityType = ActivityType.DEFAULT;
        }
        return activityType;
    }
}