package tuition;

import java.util.ArrayList;
import java.util.List;

public class Tutor {

    private String tutorID;
    private String name;
    private String qualification;
    private int experience; // in years

    // Subjects taught by this tutor (aggregation)
    private List<Subject> subjects;

    public Tutor(String tutorID, String name, String qualification, int experience) {
        this.tutorID = tutorID;
        this.name = name;
        this.qualification = qualification;
        this.experience = experience;
        this.subjects = new ArrayList<>();
    }

    public String getTutorID() {
        return tutorID;
    }

    public String getName() {
        return name;
    }

    public String getQualification() {
        return qualification;
    }

    public int getExperience() {
        return experience;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void assignSubject(Subject subject) {
        if (!subjects.contains(subject)) {
            subjects.add(subject);
            subject.setTutor(this);
            System.out.println("Assigned " + subject.getName() + " to tutor " + this.name);
        } else {
            System.out.println(subject.getName() + " already assigned to " + this.name);
        }
    }

    /**
     * View list of students across all subjects this tutor teaches
     */
    public void viewStudents() {
        System.out.println("\n--- Students for Tutor: " + this.name + " ---");
        boolean foundAny = false;
        for (Subject s : subjects) {
            System.out.println("Subject: " + s.getName());
            for (Enrollment e : s.getEnrollments()) {
                System.out.println("  - " + e.getStudent().getName() + " | Status: " + e.getStatus());
                foundAny = true;
            }
        }
        if (!foundAny) {
            System.out.println("  No students found.");
        }
    }

    // Update notes for a  student's enrollment in a subject
    public void updateNotes(Enrollment enrollment, String notes) {
        System.out.println("Note added for " + enrollment.getStudent().getName() +
                " in " + enrollment.getSubject().getName() + ": " + notes);
    }
}
