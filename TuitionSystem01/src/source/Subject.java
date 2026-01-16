package source;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class Subject {

    @JsonProperty("id")
    private String subjectId;
    @JsonProperty("name")
    private String subjectName;
    @JsonProperty("fee")
    private double monthlyFee;
    private int capacity;

    // Aggregation: tutor may be null (subject can exist without tutor)
    @JsonIgnore
    private Tutor tutor;
    private String tutorId;

    // Track enrollments for vacancy and reporting
    @JsonIgnore
    private List<Enrollment> enrollments;

    @JsonProperty("day")
    private int dayOfWeek;
    @JsonProperty("start")
    private int startTime; // Hour of day
    @JsonProperty("duration")
    private int duration; // In hours

    public Subject() {
        this.enrollments = new ArrayList<>();
    }

    public Subject(String subjectId, String subjectName, double monthlyFee, int capacity, int dayOfWeek, int startTime, int duration, String tutorId) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.monthlyFee = monthlyFee;
        this.capacity = capacity;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.duration = duration;
        this.enrollments = new ArrayList<>();
        this.tutor = null;
        this.tutorId = tutorId;
    }

    public String getSubjectId() {
        return subjectId;
    }
    
    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }
    
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public double getMonthlyFee() {
        return monthlyFee;
    }
    
    public void setMonthlyFee(double monthlyFee) {
        this.monthlyFee = monthlyFee;
    }

    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

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

    public Tutor getTutor() {
        return tutor;
    }

    public String getTutorId() {
        return tutorId;
    }
    
    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    // Adds a enrollment record to the subject
    public void addEnrollment(Enrollment enrollment) {
        this.enrollments.add(enrollment);
    }

    // checkVacancy: returns number of available seats, capacity  is active enrollments
    public int checkVacancy() {
        int activeCount = 0;
        for (Enrollment e : enrollments) {
            if (e.getStatus().equalsIgnoreCase("Active")) activeCount++;
        }
        int vacancy = capacity - activeCount;
        return Math.max(vacancy, 0);
    }

    // Returns a brief tutor detail string; can be null if no tutor assigned
    @JsonIgnore
    public String getTutorDetails() {
        if (tutor == null) return "No tutor assigned.";
        return tutor.getFullName() + " (" + tutor.getQualification() + ")";
    }

    @JsonIgnore
    public String getDisplayDetails() {
        String[] days = {"", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        return String.format("%s - %s | RM%.2f | %s %02d:00 (%dh) | Vacancy: %d | Tutor: %s",
                subjectId, subjectName, monthlyFee, days[dayOfWeek], startTime, duration, checkVacancy(), (tutor == null ? "None" : tutor.getFullName()));
    }
}
