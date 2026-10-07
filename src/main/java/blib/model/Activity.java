package blib.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import blib.enums.ActivityType;


public class Activity implements Serializable {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final long serialVersionUID = 1L;
    private int membershipNumber;
    private ActivityType type;
    private int entityId;
    private LocalDateTime activityDateTime;


    public Activity(int membershipNumber, ActivityType type, int entityId,
                    LocalDateTime activityDateTime) {
        this.membershipNumber = membershipNumber;
        this.type = type;
        this.entityId = entityId;
        this.activityDateTime = activityDateTime;
    }

    public Activity(){};

    public int getMembershipNumber() { return membershipNumber; }
    public void setMembershipNumber(int membershipNumber) { this.membershipNumber = membershipNumber; }

    public ActivityType getActivityType() { return type; }
    public void setActivityType(ActivityType activityType) { this.type = activityType; }

    public int getEntityId() {return entityId; }
    public void setEntityId(int entityId) { this.entityId = entityId; }

    public LocalDateTime getActivityDateTime() { return activityDateTime; }
    public void setActivityDateTime(LocalDateTime activityDateTime) { this.activityDateTime = activityDateTime; }

    @Override
    public String toString() {
        return String.format("Name: %s, Target ID: %d, Date: %s",
                type.getValue(), entityId, activityDateTime.format(formatter));
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Activity activity = (Activity) obj;

        if (membershipNumber != activity.membershipNumber) return false;
        if (!type.getValue().equals(activity.getActivityType().getValue())) return false;
        return activityDateTime.equals(activity.activityDateTime);
    }


    @Override
    public int hashCode() {
        int result = membershipNumber;
        result = 31 * result + entityId;
        result = 31 * result + activityDateTime.hashCode();
        return result;
    }
}