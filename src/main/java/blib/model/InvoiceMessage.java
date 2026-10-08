package blib.model;

import java.io.Serializable;
import java.util.Date;

import blib.enums.IsRead;
import blib.enums.InvoiceSubject;

public class InvoiceMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private int msgId;
    private int memberId;
    private String username;
    private String name;
    private InvoiceSubject subject;
    private String content;
    private Date date;
    private IsRead isRead;

    public InvoiceMessage(int msgId, int memberId, String username, String name, InvoiceSubject subject,
                          String content, Date date, IsRead isRead) {
        this.msgId = msgId;
        this.memberId = memberId;
        this.username = username;
        this.name = name;
        this.subject = subject;
        this.content = content;
        this.date = date;
        this.isRead = isRead;
    }

    public int getMessageID() { return this.msgId; }

    public int getMemberId() { return this.memberId; }

    public String getUsername() { return this.username; }

    public String getName() { return this.name; }

    public InvoiceSubject getSubject() { return this.subject; }

    public String getContent() { return this.content; }

    public Date getMessageDate() { return this.date; }

    public IsRead getIsRead() { return this.isRead; }

    public String toString() {
        return "(InvoiceMessage) " + this.getSubject().toString() + "(id: " + this.getMessageID() + ", memberID: " +
                this.getMemberId() + ", username: " + this.getUsername() + ", name: " + this.getName() +
                ", read?: " + this.getIsRead().toString() + ", date: " + this.getMessageDate().toString() + ")";
    }
}