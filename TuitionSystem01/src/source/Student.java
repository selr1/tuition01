package source;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class Student extends User {

    @JsonProperty("id")
    private String studentID;
    private String phone;
    private String level;
    @JsonProperty("parent")
    private String parentContact;

    // Association: a student can have many enrollments
    @JsonIgnore
    private List<Enrollment> enrollments;

    // Composition: timetable belongs to student
    @JsonIgnore
    private Timetable timetable;

    public Student() {
        super();
        this.enrollments = new ArrayList<>();
        this.timetable = new Timetable(this);
    }

    public Student(String name, String username, String password, String studentID, String phone, String level, String parentContact) {
        super(studentID, name, username, password, "Student");
        this.studentID = studentID.startsWith("S_") ? studentID : "S_" + studentID;
        this.phone = phone;
        this.level = level;
        this.parentContact = parentContact;
        this.enrollments = new ArrayList<>();
        this.timetable = new Timetable(this); // composition
    }

    @Override
    public String getUserId() {
        return studentID;
    }

    public String getStudentID() {
        return studentID;
    }
    
    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLevel() {
        return level;
    }
    
    public void setLevel(String level) {
        this.level = level;
    }

    public String getParentContact() {
        return parentContact;
    }
    
    public void setParentContact(String parentContact) {
        this.parentContact = parentContact;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public Timetable getTimetable() {
        return timetable;
    }

    // Enrolls this student into a subject by creating an Enrollment record.
 
    public void enrollClass(Subject subject) {
        // Prevent duplicate active enrollments for same subject
        for (Enrollment e : enrollments) {
            if (e.getSubject().equals(subject) && e.getStatus().equalsIgnoreCase("Active")) {
                System.out.println("You are already enrolled in " + subject.getSubjectName() + ".");
                return;
            }
        }
        Enrollment newEnrollment = new Enrollment(this, subject);
        this.enrollments.add(newEnrollment);
        subject.addEnrollment(newEnrollment);
        System.out.println("Successfully enrolled in: " + subject.getSubjectName());
    }

    // View total monthly fees for all enrolled subjects for student
  
    public double viewFees() {
        double total = 0.0;
        for (Enrollment e : enrollments) {
            if (e.getStatus().equalsIgnoreCase("Active")) {
                total += e.calculateFee();
            }
        }
        System.out.println(String.format("Total monthly fees for %s: RM%.2f", this.getFullName(), total));
        return total;
    }

    //Prints the student's timetable
     
    public void viewSchedule() {
        System.out.println("\n--- Timetable for " + this.getFullName() + " ---");
        timetable.printTimetable();
    }
}
