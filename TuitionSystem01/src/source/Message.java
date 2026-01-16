package source;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class Message {
    private String enrollmentId;
    private String senderId; // "Student" or "Tutor" (or specific ID if needed, but role is usually enough for display if we know who is who)
    // Actually, senderId should probably be the User ID to be precise, or just a name/role string. 
    // The previous implementation used strings like "Student: hello". 
    // Let's use senderId as the User ID for robustness, and we can look up the name. 
    // Or to keep it simple as per previous "Student: msg" format, we can store senderName.
    // Let's store senderId and senderName for flexibility.
    
    private String senderName;
    private String content;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public Message() {}

    public Message(String enrollmentId, String senderId, String senderName, String content) {
        this.enrollmentId = enrollmentId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(String enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
