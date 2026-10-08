package blib.dto;

public class ActivityRequest {
    private int memberId;
    private String type;
    private int affectedEntityId;

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public String getActivityType() { return type; }
    public void setActivityType(String type) { this.type = type; }

    public int getAffectedEntityId() { return affectedEntityId; }
    public void setAffectedEntityId(int affectedEntityId) { this.affectedEntityId = affectedEntityId; }
}