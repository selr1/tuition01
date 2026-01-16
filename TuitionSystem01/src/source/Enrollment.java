package source;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Enrollment {

    private static int counter = 1;

    public static void setCounter(int count) {
        counter = count;
    }

    @JsonProperty("enrollmentID")
    private String enrollmentId;
    @JsonIgnore
    private Student student;
    @JsonIgnore
    private Subject subject;
    
    // For JSON serialization/deserialization
    @JsonProperty("studentId")
    private String studentId;
    @JsonProperty("subjectId")
    private String subjectId;
    @JsonProperty("tutorName")
    private String tutorName;
    
    @JsonProperty("dateEnrolled")
    private LocalDate timestamp;
    private String status; // Active, Pending, Discussing, Approved, Rejected

    @JsonProperty("day")
    private int dayOfWeek;
    @JsonProperty("start")
    private int startTime; // Hour of day
    @JsonProperty("duration")
    private int duration; // In hours
    
    @JsonProperty("progress")
    private String progress; // Tutor notes/progress report


    public Enrollment() {
    }

    public Enrollment(Student student, Subject subject) {
        this.enrollmentId = "ENR" + counter++;
        this.student = student;
        this.subject = subject;
        this.studentId = student.getStudentID();
        this.subjectId = subject.getSubjectId();
        this.tutorName = subject.getTutor() != null ? subject.getTutor().getFullName() : "Unknown";
        this.timestamp = LocalDate.now();
        this.status = "Pending"; // Default to Pending for new requests
        this.timestamp = LocalDate.now();
        this.status = "Pending"; // Default to Pending for new requests
    }
    
    // Constructor for loading from JSON
    public Enrollment(String studentId, String subjectId, String tutorName, LocalDate date, String status) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.tutorName = tutorName;
        this.timestamp = date;
        this.status = status;
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public Student getStudent() {
        return student;
    }
    
    public void setStudent(Student student) {
        this.student = student;
    }

    public Subject getSubject() {
        return subject;
    }
    
    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }
    


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void updateStatus(String newStatus) {
        this.status = newStatus;
        System.out.println("Enrollment " + enrollmentId + " status updated to: " + newStatus);
    }
    


    public double calculateFee() {
        return subject != null ? subject.getMonthlyFee() : 0.0;
    }
    
    // Getters for JSON properties
    public String getStudentId() { return studentId; }
    public String getSubjectId() { return subjectId; }
    public String getTutorName() { return tutorName; }
    
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public void setTutorName(String tutorName) { this.tutorName = tutorName; }

    public int getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public int getStartTime() { return startTime; }
    public void setStartTime(int startTime) { this.startTime = startTime; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    @JsonIgnore
    public int getEndTime() {
        return startTime + duration;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }
}
