package source;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tutor extends User {

    private String tutorID;
    private String qualification;
    private int experience; // in years

    // Subjects taught by this tutor (aggregation)
    @JsonIgnore
    private List<Subject> subjects;

    public Tutor() {
        super();
        this.subjects = new ArrayList<>();
    }

    public Tutor(String tutorID, String name, String username, String password, String qualification, int experience) {
        super(tutorID, name, username, password, "Tutor");
        this.tutorID = tutorID;
        this.qualification = qualification;
        this.experience = experience;
        this.subjects = new ArrayList<>();
    }

    @Override
    public String getUserId() {
        return tutorID;
    }

    public String getTutorID() {
        return tutorID;
    }
    
    public void setTutorID(String tutorID) {
        this.tutorID = tutorID;
    }

    public String getQualification() {
        return qualification;
    }
    
    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public int getExperience() {
        return experience;
    }
    
    public void setExperience(int experience) {
        this.experience = experience;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void assignSubject(Subject subject) {
        if (!subjects.contains(subject)) {
            subjects.add(subject);
            subject.setTutor(this);
            System.out.println("Assigned " + subject.getSubjectName() + " to tutor " + this.getFullName());
        } else {
            System.out.println(subject.getSubjectName() + " already assigned to " + this.getFullName());
        }
    }

    /**
     * View list of students across all subjects this tutor teaches
     */
    public void viewStudents() {
        System.out.println("\n--- Students for Tutor: " + this.getFullName() + " ---");
        boolean foundAny = false;
        for (Subject s : subjects) {
            System.out.println("Subject: " + s.getSubjectName());
            for (Enrollment e : s.getEnrollments()) {
                System.out.println("  - " + e.getStudent().getFullName() + " | Status: " + e.getStatus());
                foundAny = true;
            }
        }
        if (!foundAny) {
            System.out.println("  No students found.");
        }
    }

    // Update notes for a  student's enrollment in a subject
    public void updateNotes(Enrollment enrollment, String notes) {
        System.out.println("Note added for " + enrollment.getStudent().getFullName() +
                " in " + enrollment.getSubject().getSubjectName() + ": " + notes);
    }
}
