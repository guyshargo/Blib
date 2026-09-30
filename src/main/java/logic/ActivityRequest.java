package logic;

public class ActivityRequest {
    private int membershipNumber;
    private String activityType;
    private String description;

    public int getMembershipNumber() { return membershipNumber; }
    public void setMembershipNumber(int membershipNumber) { this.membershipNumber = membershipNumber; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}