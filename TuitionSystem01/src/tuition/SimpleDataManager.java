package tuition;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleDataManager {

    private static final String USERS_FILE = "/home/zha/NetBeansProjects/integrated/src/JSONs/users.json";
    private static final String ENROLLMENTS_FILE = "/home/zha/NetBeansProjects/integrated/src/JSONs/enrollments.json";
    private static final String SUBJECTS_FILE = "/home/zha/NetBeansProjects/integrated/src/JSONs/subjects.json";

    public static void saveUser(String username, String password, String role) {
        String json = String.format("{\"username\":\"%s\", \"password\":\"%s\", \"role\":\"%s\"},",
                username, password, role);
        appendToFile(USERS_FILE, json);
    }

    public static String authenticate(String input, String password, String role) {
        try {
            if (!Files.exists(Paths.get(USERS_FILE))) return null;
            String content = new String(Files.readAllBytes(Paths.get(USERS_FILE)));
            content = content.replace("[", "").replace("]", "").replace("\n", " ");
            String[] objects = content.split("\\},");
            for (String obj : objects) {
                if (!obj.contains("username")) continue;
                String u = extractRegex(obj, "username");
                String id = extractRegex(obj, "id");
                String p = extractRegex(obj, "password");
                String r = extractRegex(obj, "role");
                
                // Check if input matches username OR id
                boolean userMatch = u.equals(input) || (r.equalsIgnoreCase("Student") && id.equals(input));
                
                if (userMatch && p.equals(password) && r.equalsIgnoreCase(role)) {
                    // Return canonical ID: for students it's the ID, for tutors it's the username (as they don't have separate ID yet)
                    if (r.equalsIgnoreCase("Student")) {
                         return id != null && !id.isEmpty() ? id : u;
                    } else {
                        return u;
                    }
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static void saveStudent(Student student, String username, String password) {
        // Save everything into USERS_FILE
        String json = String.format("{\"username\":\"%s\", \"password\":\"%s\", \"role\":\"Student\", \"id\":\"%s\", \"name\":\"%s\", \"phone\":\"%s\", \"level\":\"%s\", \"parent\":\"%s\"},",
                username, password, student.getStudentID(), student.getName(), student.getPhone(), student.getLevel(), student.getParentContact());
        appendToFile(USERS_FILE, json);
    }

    public static void saveEnrollment(String studentId, String subjectId) {
        // Find subject to get tutorId
        List<Subject> subjects = loadSubjects();
        String tutorName = "Unknown";
        for(Subject s : subjects) {
            if(s.getSubjectID().equals(subjectId)) {
                tutorName = getTutorName(s.getTutorId());
                break;
            }
        }
        
        String json = String.format("{\"studentId\":\"%s\", \"subjectId\":\"%s\", \"tutorName\":\"%s\", \"date\":\"%s\", \"status\":\"Active\"}",
                studentId, subjectId, tutorName, java.time.LocalDate.now());
        appendToFile(ENROLLMENTS_FILE, json);
    }

    public static List<Subject> loadSubjects() {
        List<Subject> subjects = new ArrayList<>();
        try {
            if (!Files.exists(Paths.get(SUBJECTS_FILE))) return subjects;
            String content = new String(Files.readAllBytes(Paths.get(SUBJECTS_FILE)));
            content = content.replace("[", "").replace("]", "").replace("\n", " ");
            String[] objects = content.split("\\},");
            for (String obj : objects) {
                if (!obj.contains("id")) continue;
                String id = extractRegex(obj, "id");
                String name = extractRegex(obj, "name");
                double fee = Double.parseDouble(extractRegex(obj, "fee"));
                int cap = Integer.parseInt(extractRegex(obj, "capacity"));
                int day = Integer.parseInt(extractRegex(obj, "day"));
                int start = Integer.parseInt(extractRegex(obj, "start"));
                int dur = Integer.parseInt(extractRegex(obj, "duration"));
                String tId = extractRegex(obj, "tutorId");
                subjects.add(new Subject(id, name, fee, cap, day, start, dur, tId));
            }
        } catch (Exception e) {
        }
        return subjects;
    }

    public static List<Enrollment> loadEnrollmentsForStudent(String studentId) {
        List<Enrollment> enrollments = new ArrayList<>();
        try {
            if (!Files.exists(Paths.get(ENROLLMENTS_FILE))) return enrollments;
            
            // Resolve aliases (ID and Username)
            String targetId = studentId;
            String targetUsername = "";
            
            if (Files.exists(Paths.get(USERS_FILE))) {
                String content = new String(Files.readAllBytes(Paths.get(USERS_FILE)));
                content = content.replace("[", "").replace("]", "").replace("\n", " ");
                String[] objects = content.split("\\},");
                for (String obj : objects) {
                    if (!obj.contains("username")) continue;
                    String u = extractRegex(obj, "username");
                    String id = extractRegex(obj, "id");
                    
                    // Check matches: exact ID, exact Username, or ID without "S_" matching Username (for cases where ID is generated from Username)
                    String rawInputId = studentId.startsWith("S_") ? studentId.substring(2) : studentId;
                    
                    if (studentId.equals(id) || studentId.equals(u) || rawInputId.equals(u)) {
                        targetId = id;
                        targetUsername = u;
                        break;
                    }
                }
            }
            
            String content = new String(Files.readAllBytes(Paths.get(ENROLLMENTS_FILE)));
            String[] objects = content.split("\\}");
            
            List<Subject> allSubjects = loadSubjects();
            Student dummyStudent = new Student("Dummy", studentId, studentId, "", "", "");

            for (String obj : objects) {
                if (!obj.contains("studentId")) continue;
                String sId = extractRegex(obj, "studentId");
                
                // Match against either ID or Username
                boolean match = sId.equals(targetId) || (!targetUsername.isEmpty() && sId.equals(targetUsername));
                
                if (match) {
                    String subId = extractRegex(obj, "subjectId");
                    Subject subj = allSubjects.stream().filter(s -> s.getSubjectID().equals(subId)).findFirst().orElse(null);
                    if (subj != null) {
                        enrollments.add(new Enrollment(dummyStudent, subj));
                    }
                }
            }
        } catch (Exception e) {
        }
        return enrollments;
    }

    public static String getTutorName(String tutorId) {
        try {
            if (!Files.exists(Paths.get(USERS_FILE))) return tutorId;
            String content = new String(Files.readAllBytes(Paths.get(USERS_FILE)));
            content = content.replace("[", "").replace("]", "").replace("\n", " ");
            String[] objects = content.split("\\},");
            for (String obj : objects) {
                if (!obj.contains("username")) continue;
                String u = extractRegex(obj, "username");
                String r = extractRegex(obj, "role");
                if (u.equals(tutorId) && r.equalsIgnoreCase("Tutor")) {
                    // For now, username is the name for tutors as we don't have separate name field for tutors in users.json yet
                    // But wait, users.json structure for tutor is just username/password/role. 
                    // Let's just return username.
                    return u;
                }
            }
        } catch (Exception e) {
        }
        return tutorId;
    }

    public static List<String> fetchTutorStudentEntries(String tutorId) {
        List<String> entries = new ArrayList<>();
        try {
            if (!Files.exists(Paths.get(ENROLLMENTS_FILE))) return entries;
            String content = new String(Files.readAllBytes(Paths.get(ENROLLMENTS_FILE)));
            content = content.replace("[", "").replace("]", "").replace("\n", " ");
            String[] objects = content.split("\\},");
            
            List<Subject> allSubjects = loadSubjects();
            List<Student> allStudents = loadAllStudents();

            for (String obj : objects) {
                String tName = extractRegex(obj, "tutorName");
                String subId = extractRegex(obj, "subjectId");
                
                // Fallback: if tutorName is missing, check subject's tutor
                if (tName.isEmpty()) {
                    Subject s = allSubjects.stream().filter(sub -> sub.getSubjectID().equals(subId)).findFirst().orElse(null);
                    if (s != null && s.getTutorId().equals(tutorId)) {
                        tName = tutorId; // Match found via subject
                    }
                }

                if (tName.equals(tutorId)) {
                    String sId = extractRegex(obj, "studentId");
                    
                    // Find Student
                    Student student = allStudents.stream().filter(st -> 
                        st.getStudentID().equals(sId) || st.getUsername().equals(sId)
                    ).findFirst().orElse(null);
                    
                    // Find Subject
                    Subject subject = allSubjects.stream().filter(sub -> sub.getSubjectID().equals(subId)).findFirst().orElse(null);
                    
                    if (student != null && subject != null) {
                        String dayStr = "";
                        switch(subject.getDay()) {
                            case 1: dayStr = "Mon"; break;
                            case 2: dayStr = "Tue"; break;
                            case 3: dayStr = "Wed"; break;
                            case 4: dayStr = "Thu"; break;
                            case 5: dayStr = "Fri"; break;
                            case 6: dayStr = "Sat"; break;
                            case 7: dayStr = "Sun"; break;
                        }
                        
                        String entry = String.format("ID: %s | Name: %s | Subject: %s | %s %02d:00", 
                                student.getStudentID(), student.getName(), subject.getName(), dayStr, subject.getStartHour());
                        entries.add(entry);
                    }
                }
            }
        } catch (Exception e) {
        }
        return entries;
    }

    private static List<Student> loadAllStudents() {
        List<Student> students = new ArrayList<>();
        try {
            if (!Files.exists(Paths.get(USERS_FILE))) return students;
            String content = new String(Files.readAllBytes(Paths.get(USERS_FILE)));
            content = content.replace("[", "").replace("]", "").replace("\n", " ");
            String[] objects = content.split("\\},");
            for (String obj : objects) {
                if (!obj.contains("role")) continue;
                String role = extractRegex(obj, "role");
                if(role.equalsIgnoreCase("Student")) {
                    String id = extractRegex(obj, "id");
                    String name = extractRegex(obj, "name");
                    String phone = extractRegex(obj, "phone");
                    String level = extractRegex(obj, "level");
                    String parent = extractRegex(obj, "parent");
                    // Note: Student constructor expects ID without "S_" prefix if it adds it, but our stored ID has "S_"
                    // Let's adjust Student constructor or pass raw ID. 
                    // Existing Student constructor: this.studentID = "S_" + studentID;
                    // So we should strip "S_" if present or adjust constructor.
                    // For safety, let's just pass it as is and fix Student class if needed, OR strip it here.
                    // The stored ID in users.json is "S_..."
                    // If we pass "S_..." to new Student("...", "S_...", ...), it becomes "S_S_..."
                    // We need to handle this.
                    String u = extractRegex(obj, "username");
                    
                    // Fallback: if ID is missing, use username
                    if (id.isEmpty()) {
                        id = u;
                    }
                    
                    String rawId = id.startsWith("S_") ? id.substring(2) : id;
                    students.add(new Student(name, u, rawId, phone, level, parent));
                }
            }
        } catch (Exception e) {
        }
        return students;
    }

    private static void appendToFile(String filePath, String content) {
        try {
            if (!Files.exists(Paths.get(filePath))) {
                Files.createFile(Paths.get(filePath));
                Files.write(Paths.get(filePath), "[\n".getBytes(), StandardOpenOption.APPEND);
            }
            
            // Read existing content to check if we need a comma
            String existing = new String(Files.readAllBytes(Paths.get(filePath)));
            existing = existing.trim();
            
            if (existing.endsWith("]")) {
                // Remove the last ]
                existing = existing.substring(0, existing.length() - 1);
                // If it's not just "[", add a comma
                if (!existing.trim().endsWith("[")) {
                     existing += ",";
                }
                // Write back with new content and closing ]
                Files.write(Paths.get(filePath), (existing + "\n" + content + "\n]").getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            } else {
                // Fallback for empty or malformed file, just append
                Files.write(Paths.get(filePath), (content + "\n").getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
        }
    }

    private static String extractRegex(String source, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"?([^,\"}]+)\"?");
        Matcher matcher = pattern.matcher(source);
        return matcher.find() ? matcher.group(1).trim() : "";
    }
}
