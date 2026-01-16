package test;

import source.*;
import java.util.List;

public class WorkflowTest {
    public static void main(String[] args) {
        System.out.println("=== Workflow Verification Test ===");
        
        DataManager dm = DataManager.getInstance();
        
        // 1. Create a Request
        System.out.println("\n[1] Creating Request...");
        Student student = (Student) dm.getUser("S_12345");
        Subject subject = dm.getAllSubjects().stream().filter(s -> s.getSubjectId().equals("OOP")).findFirst().orElse(null);
        
        if (student != null && subject != null) {
            Enrollment en = new Enrollment(student, subject);
            if (dm.saveEnrollment(en)) {
                System.out.println("Request created: " + en.getEnrollmentId());
                
                // 2. Send Message
                System.out.println("\n[2] Sending Message...");
                Message msg = new Message(en.getEnrollmentId(), student.getUserId(), student.getFullName(), "Can I join?");
                dm.saveMessage(msg);
                System.out.println("Message saved.");
                
                // 3. Verify Message Retrieval
                System.out.println("\n[3] Retrieving Messages...");
                List<Message> messages = dm.getMessagesForEnrollment(en.getEnrollmentId());
                for (Message m : messages) {
                    System.out.println(" - " + m.getSenderName() + ": " + m.getContent());
                }
                
                // 4. Tutor Approves and Schedules
                System.out.println("\n[4] Scheduling...");
                subject.setDayOfWeek(1); // Monday
                subject.setStartTime(10); // 10:00
                dm.saveSubject(subject);
                dm.updateEnrollmentStatus(en.getEnrollmentId(), "Approved");
                System.out.println("Subject scheduled and enrollment approved.");
                
                // 5. Verify Subject Update
                System.out.println("\n[5] Verifying Subject...");
                Subject updatedSub = dm.getAllSubjects().stream().filter(s -> s.getSubjectId().equals("OOP")).findFirst().orElse(null);
                System.out.println("Subject Day: " + updatedSub.getDayOfWeek());
                System.out.println("Subject Start: " + updatedSub.getStartTime());
                
            } else {
                System.out.println("Request already exists or failed.");
            }
        } else {
            System.out.println("Student or Subject not found.");
        }
    }
}
