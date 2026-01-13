package tuition;

import java.time.LocalDate;

public class Enrollment {

    private static int counter = 1;

    private String enrollmentID;
    private Student student;
    private Subject subject;
    private LocalDate dateEnrolled;
    private String status; // active, dropped, completed, Pending

    public Enrollment(Student student, Subject subject) {
        this.enrollmentID = "ENR" + counter++;
        this.student = student;
        this.subject = subject;
        this.dateEnrolled = LocalDate.now();
        this.status = "Active";
    }

    public String getEnrollmentID() {
        return enrollmentID;
    }

    public Student getStudent() {
        return student;
    }

    public Subject getSubject() {
        return subject;
    }

    public LocalDate getDateEnrolled() {
        return dateEnrolled;
    }

    public String getStatus() {
        return status;
    }

    //Changes the enrollment status

    public void updateStatus(String newStatus) {
        this.status = newStatus;
        System.out.println("Enrollment " + enrollmentID + " status updated to: " + newStatus);
    }

    //Calculates fee for this particular subject.
    
    public double calculateFee() {
        return subject.getFee();
    }
}
