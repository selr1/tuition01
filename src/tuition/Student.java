package tuition;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {

    private String studentID;
    private String phone;
    private String level;
    private String parentContact;

    // Association: a student can have many enrollments
    private List<Enrollment> enrollments;

    // Composition: timetable belongs to student
    private Timetable timetable;

    public Student(String name, String username, String studentID, String phone, String level, String parentContact) {
        super(name, username);
        this.studentID = "S_" + studentID;
        this.phone = phone;
        this.level = level;
        this.parentContact = parentContact;
        this.enrollments = new ArrayList<>();
        this.timetable = new Timetable(this); // composition
    }

    public String getStudentID() {
        return studentID;
    }

    public String getPhone() {
        return phone;
    }

    public String getLevel() {
        return level;
    }

    public String getParentContact() {
        return parentContact;
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
                System.out.println("You are already enrolled in " + subject.getName() + ".");
                return;
            }
        }
        Enrollment newEnrollment = new Enrollment(this, subject);
        this.enrollments.add(newEnrollment);
        subject.addEnrollment(newEnrollment);
        System.out.println("Successfully enrolled in: " + subject.getName());
    }

    // View total monthly fees for all enrolled subjects for student
  
    public double viewFees() {
        double total = 0.0;
        for (Enrollment e : enrollments) {
            if (e.getStatus().equalsIgnoreCase("Active")) {
                total += e.calculateFee();
            }
        }
        System.out.println(String.format("Total monthly fees for %s: RM%.2f", this.getName(), total));
        return total;
    }

    //Prints the student's timetable
     
    public void viewSchedule() {
        System.out.println("\n--- Timetable for " + this.getName() + " ---");
        timetable.printTimetable();
    }
}
