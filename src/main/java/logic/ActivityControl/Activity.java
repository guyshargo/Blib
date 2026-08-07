package logic.ActivityControl;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Description:
 * Class for the activities that have been done in the system
 */
public class Activity implements Serializable {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final long serialVersionUID = 1L; // Serial version UID for serialization
    private int membershipNumber;               // Foreign Key to Member
    private ActivityType type;                // Type of the activity
    private String activityDescription;         // Detailed description of the activity
    private LocalDateTime activityDateTime;     // When the activity occurred (DATETIME)

    /**
     * Description:
     * Method for constructing the given class
     *
     * @param membershipNumber    int
     * @param type                ActivityType.class
     * @param activityDescription String.class
     * @param activityDateTime    LocalDate.class
     */
    public Activity(int membershipNumber, ActivityType type, String activityDescription,
                    LocalDateTime activityDateTime) {
        this.membershipNumber = membershipNumber;
        this.type = type;
        this.activityDescription = activityDescription;
        this.activityDateTime = activityDateTime;
    }

    /**
     * Description:
     * Getter method for the activity's member id
     *
     * @return membershipNumber int
     */
    public int getMembershipNumber() {
        return membershipNumber;
    }

    /**
     * Description:
     * Setter method for the activity's member id
     *
     * @param membershipNumber int
     */
    public void setMembershipNumber(int membershipNumber) {
        this.membershipNumber = membershipNumber;
    }

    /**
     * Description:
     * Getter method for the activity's type
     *
     * @return activityName ActivityType.class
     */
    public ActivityType getActivityType() {
        return type;
    }

    /**
     * Description:
     * Getter method for the activity's description
     *
     * @return activityDescription String.class
     */
    public String getActivityDescription() {
        return activityDescription;
    }

    /**
     * Description:
     * Getter method for the activity's date
     *
     * @return activityDateTime LocalDateTime.class
     */
    public LocalDateTime getActivityDateTime() {
        return activityDateTime;
    }

    /**
     * Description:
     * Method for generating a string representing the given class object
     *
     * @return String.class
     */
    @Override
    public String toString() {
        return String.format("Name: %s, Description: %s, Date: %s"
                , type.getValue(), activityDescription, activityDateTime.format(formatter));
    }

    /**
     * Description:
     * Method for checking if given object is equal to the current object calling
     *
     * @param obj Object.class
     * @return boolean (true if the given object is equal, else return false)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Activity activity = (Activity) obj;

        if (membershipNumber != activity.membershipNumber) return false;
        if (!type.getValue().equals(activity.getActivityType().getValue())) return false;
        return activityDateTime.equals(activity.activityDateTime);
    }

    /**
     * Description:
     * Method for generating a unique hashcode to current object of class
     *
     * @return result int
     */
    @Override
    public int hashCode() {
        int result = membershipNumber;
        result = 31 * result + type.hashCode();
        result = 31 * result + activityDateTime.hashCode();
        return result;
    }
}