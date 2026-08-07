package logic.ReportControl;

import logic.FreezeStatus;

import java.time.LocalDate;

/**
 * Description:
 * Class for the member's status change report in the system
 */
public class MemberStatusChange {
    private int memberId;
    private String memberName;
    private FreezeStatus memberStatus;
    private LocalDate changeStatusDate;

    /**
     * Description:
     * Method for constructing the given class
     *
     * @param memberId         int
     * @param memberName       String.class
     * @param memberStatus     FreezeStatus.class
     * @param changeStatusDate LocalDate.class
     */
    public MemberStatusChange(int memberId, String memberName, FreezeStatus memberStatus, LocalDate changeStatusDate) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.memberStatus = memberStatus;
        this.changeStatusDate = changeStatusDate;
    }

    /**
     * Description:
     * Getter method for the member status report's member name
     *
     * @return memberName String.class
     */
    public String getMemberName() {
        return memberName;
    }

    /**
     * Description:
     * Getter method for the member status report's member id
     *
     * @return memberId int
     */
    public int getMemberId() {
        return memberId;
    }

    /**
     * Description:
     * Getter method for the member status report's member freeze status
     *
     * @return memberStatus FreezeStatus.class
     */
    public FreezeStatus getMemberStatus() {
        return memberStatus;
    }

    /**
     * Description:
     * Getter method for the member status report's date
     *
     * @return changeStatusDate LocalDate.class
     */
    public LocalDate getChangeStatusDate() {
        return changeStatusDate;
    }
}