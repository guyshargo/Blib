package logic;

import java.io.Serializable;

public class Subscriber implements Serializable {
	private static final long serialVersionUID = 1L;
	private int subscriber_id;
	private String subscriber_name;
	private int detailed_subscription_history;
	private String subscriber_phone_number;
	private String subscriber_email;

	// Constructor
	public Subscriber(int id, String name, int detail_history, String phone, String email) {
		this.subscriber_id = id;
		this.subscriber_name = name;
		this.detailed_subscription_history = detail_history;
		this.subscriber_phone_number = phone;
		this.subscriber_email = email;
	}
	
	//setters and getters
	
	public int getSubscriber_id() {
		return subscriber_id;
	}

	public void setSubscriber_id(int subscriber_id) {
		this.subscriber_id = subscriber_id;
	}

	public String getSubscriber_name() {
		return subscriber_name;
	}

	public void setSubscriber_name(String subscriber_name) {
		this.subscriber_name = subscriber_name;
	}

	public int getDetailed_subscription_history() {
		return detailed_subscription_history;
	}

	public void setDetailed_subscription_history(int detailed_subscription_history) {
		this.detailed_subscription_history = detailed_subscription_history;
	}

	public String getSubscriber_phone_number() {
		return subscriber_phone_number;
	}

	public void setSubscriber_phone_number(String subscriber_phone_number) {
		this.subscriber_phone_number = subscriber_phone_number;
	}

	public String getSubscriber_email() {
		return subscriber_email;
	}

	public void setSubscriber_email(String subscriber_email) {
		this.subscriber_email = subscriber_email;
	}

	@Override
	public String toString() {
		return String.format("Id: %d, name: %s, history: %d, phone number: %s, email: %s", this.subscriber_id,
				this.subscriber_name, this.detailed_subscription_history, this.subscriber_phone_number,
				this.subscriber_email);
	}
}