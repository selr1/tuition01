package source;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataManager {

    private static DataManager instance;
    private final ObjectMapper mapper;
    
    // Relative paths
    private static final String USERS_FILE = "src/data/users.json";
    private static final String SUBJECTS_FILE = "src/data/subjects.json";
    private static final String ENROLLMENTS_FILE = "src/data/enrollments.json";
    private static final String MESSAGES_FILE = "src/data/messages.json";

    private List<User> users;
    private List<Subject> subjects;
    private List<Enrollment> enrollments;
    private List<Message> messages;

    private DataManager() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        loadAllData();
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    private void loadAllData() {
        users = loadList(USERS_FILE, new TypeReference<List<User>>() {});
        subjects = loadList(SUBJECTS_FILE, new TypeReference<List<Subject>>() {});
        enrollments = loadList(ENROLLMENTS_FILE, new TypeReference<List<Enrollment>>() {});
        messages = loadList(MESSAGES_FILE, new TypeReference<List<Message>>() {});
        
        // Cross-reference logic: Link objects
        linkData();
    }
    
    private <T> List<T> loadList(String path, TypeReference<List<T>> typeRef) {
        try {
            File file = new File(path);
            if (!file.exists()) return new ArrayList<>();
            return mapper.readValue(file, typeRef);
        } catch (IOException e) {
            System.err.println("Error loading data from " + path + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private void linkData() {
        // Link Tutors to Subjects
        for (Subject s : subjects) {
            if (s.getTutorId() != null) {
                Tutor t = (Tutor) users.stream()
                        .filter(u -> u instanceof Tutor && ((Tutor)u).getUserId() != null && ((Tutor)u).getUserId().equals(s.getTutorId()))
                        .findFirst().orElse(null);
                s.setTutor(t);
                if (t != null) t.assignSubject(s);
            }
        }
        
        // Link Students and Subjects to Enrollments
        for (Enrollment e : enrollments) {
            Student st = (Student) users.stream()
                    .filter(u -> u instanceof Student && ((Student)u).getUserId() != null && ((Student)u).getUserId().equals(e.getStudentId()))
                    .findFirst().orElse(null);
            e.setStudent(st);
            if (st != null) st.getEnrollments().add(e);
            
            Subject sub = subjects.stream()
                    .filter(s -> s.getSubjectId() != null && s.getSubjectId().equals(e.getSubjectId()))
                    .findFirst().orElse(null);
            e.setSubject(sub);
            if (sub != null) sub.addEnrollment(e);
        }
    }

    // --- Saving Methods ---

    public boolean saveEnrollment(Enrollment enrollment) {
        // Check for duplicates
        boolean exists = enrollments.stream()
                .anyMatch(e -> e.getStudentId().equals(enrollment.getStudentId()) && 
                               e.getSubjectId().equals(enrollment.getSubjectId()));
        
        if (exists) {
            System.out.println("Duplicate enrollment prevented.");
            return false;
        }
        
        enrollments.add(enrollment);
        return saveData(ENROLLMENTS_FILE, enrollments);
    }
    
    public boolean updateEnrollmentStatus(String enrollmentId, String newStatus) {
        Enrollment e = enrollments.stream()
                .filter(en -> en.getEnrollmentId().equals(enrollmentId))
                .findFirst().orElse(null);
        
        if (e != null) {
            e.setStatus(newStatus);
            return saveData(ENROLLMENTS_FILE, enrollments);
        }
        return false;
    }

    public boolean updateEnrollment(Enrollment enrollment) {
        for (int i = 0; i < enrollments.size(); i++) {
            if (enrollments.get(i).getEnrollmentId().equals(enrollment.getEnrollmentId())) {
                enrollments.set(i, enrollment);
                return saveData(ENROLLMENTS_FILE, enrollments);
            }
        }
        return false;
    }
    
    public boolean saveUser(User user) {
        users.add(user);
        return saveData(USERS_FILE, users);
    }
    
    public boolean saveMessage(Message message) {
        messages.add(message);
        return saveData(MESSAGES_FILE, messages);
    }
    
    public boolean saveSubject(Subject subject) {
        for (int i = 0; i < subjects.size(); i++) {
            if (subjects.get(i).getSubjectId().equals(subject.getSubjectId())) {
                subjects.set(i, subject);
                return saveData(SUBJECTS_FILE, subjects);
            }
        }
        return false;
    }

    private boolean saveData(String path, Object data) {
        try {
            mapper.writeValue(new File(path), data);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving data to " + path + ": " + e.getMessage());
            return false;
        }
    }

    // --- Accessors & Logic ---

    public User authenticate(String username, String password) {
        return users.stream()
                .filter(u -> (u.getUsername().equalsIgnoreCase(username) || 
                             (u instanceof Student && ((Student)u).getUserId().equalsIgnoreCase(username)) ||
                             (u instanceof Tutor && ((Tutor)u).getUserId().equalsIgnoreCase(username))) && 
                             u.getPassword().equals(password))
                .findFirst().orElse(null);
    }
    
    public User getUser(String id) {
        return users.stream()
                .filter(u -> (u instanceof Student && ((Student)u).getUserId().equals(id)) ||
                             (u instanceof Tutor && ((Tutor)u).getUserId().equals(id)))
                .findFirst().orElse(null);
    }

    public List<Subject> getAllSubjects() {
        return new ArrayList<>(subjects);
    }
    
    public List<Subject> getEnrolledSubjectsForStudent(String studentId) {
        return enrollments.stream()
                .filter(e -> e.getStudentId().equals(studentId))
                .map(Enrollment::getSubject)
                .collect(Collectors.toList());
    }
    
    public List<Enrollment> getStudentEnrollments(String studentId) {
        return enrollments.stream()
                .filter(e -> e.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    public double calculateTotalFees(String studentId) {
        return enrollments.stream()
                .filter(e -> e.getStudentId().equals(studentId) && "Approved".equalsIgnoreCase(e.getStatus()))
                .mapToDouble(Enrollment::calculateFee)
                .sum();
    }

    public List<Enrollment> getTutorRequests(String tutorId) {
        // Find subjects owned by tutor
        List<String> tutorSubjectIds = subjects.stream()
                .filter(s -> tutorId.equals(s.getTutorId()))
                .map(Subject::getSubjectId)
                .collect(Collectors.toList());
        
        return enrollments.stream()
                .filter(e -> tutorSubjectIds.contains(e.getSubjectId()) && 
                        ("Pending".equalsIgnoreCase(e.getStatus()) || "Discussing".equalsIgnoreCase(e.getStatus())))
                .collect(Collectors.toList());
    }
    
    public List<Enrollment> getTutorStudents(String tutorId) {
        List<String> tutorSubjectIds = subjects.stream()
                .filter(s -> tutorId.equals(s.getTutorId()))
                .map(Subject::getSubjectId)
                .collect(Collectors.toList());
                
        return enrollments.stream()
                .filter(e -> tutorSubjectIds.contains(e.getSubjectId()) && "Approved".equalsIgnoreCase(e.getStatus()))
                .collect(Collectors.toList());
    }
    
    public List<Message> getMessagesForEnrollment(String enrollmentId) {
        return messages.stream()
                .filter(m -> m.getEnrollmentId().equals(enrollmentId))
                .sorted((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()))
                .collect(Collectors.toList());
    }
}
