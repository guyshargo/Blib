package dto;

public class ActivityRequest {
    private int membershipNumber;
    private String activityType;
    private int entityId;

    public int getMembershipNumber() { return membershipNumber; }
    public void setMembershipNumber(int membershipNumber) { this.membershipNumber = membershipNumber; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public int getEntityId() { return entityId; }
    public void setEntityId(int entityId) { this.entityId = entityId; }
}