package logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import DBControl.mysqlConnection;

public class subscriberLogic {
	private mysqlConnection dbConnector;
	public subscriberLogic() {
		dbConnector = mysqlConnection.getInstance(); // singleton database connector
	}

	// Update subscriber's phone number and email only
	public boolean updateSubscriberContact(int id, String phoneNumber, String email) {
		Connection con = dbConnector.getConnection();
		String query = "UPDATE subscribers SET subscriber_phone_number = ?, subscriber_email = ? WHERE subscriber_id = ?";
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, phoneNumber);
			ps.setString(2, email);
			ps.setInt(3, id);
			int rowsUpdated = ps.executeUpdate();
			if (rowsUpdated > 0) {
				System.out.println("Subscriber contact updated successfully.");
				return true;
			}
			return false;
		} catch (SQLException e) {
			System.out.println("Failed to update subscriber contact: " + e.getMessage());
		}
		return false;
	}

	//fetch subscriber information via his id
	public Subscriber fetchSubscriberById(int subscriberId) {
		Connection con = dbConnector.getConnection();
		String query = "SELECT * FROM subscribers WHERE subscriber_id = ?";
		Subscriber subscriber = null;
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, subscriberId); // Set the subscriber ID in the query
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				// Map the database row to a Subscriber object
				subscriber = new Subscriber(rs.getInt("subscriber_id"), rs.getString("subscriber_name"),
						rs.getInt("detailed_subscription_history"), rs.getString("subscriber_phone_number"),
						rs.getString("subscriber_email"));
			}
		} catch (SQLException e) {
			System.out.println("Failed to fetch subscriber: " + e.getMessage());
		}
		return subscriber; // Return the subscriber
	}
}