package account.models;

import javax.persistence.*;
import java.util.Date;

@Entity
public class SecurityEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Date date;

    @Enumerated(EnumType.STRING)
    private SecurityEventName action;


    private String subject;

    private String object;

    private String path;

    public SecurityEvent() { }

    public SecurityEvent(Date date, SecurityEventName action, String subject, String object, String path) {
        this.date = date;
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = path;
    }

    public long getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public SecurityEventName getAction() {
        return action;
    }

    public void setAction(SecurityEventName action) {
        this.action = action;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String user) {
        this.subject = user;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
