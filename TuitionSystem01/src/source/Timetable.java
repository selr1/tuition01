package source;

import java.util.ArrayList;
import java.util.List;

public class Timetable {

    public static class TimetableRow {
        private String day;
        private String time;
        private Subject subject;
        private Tutor tutor;
        private String location;

        public TimetableRow(String day, String time, Subject subject, Tutor tutor, String location) {
            this.day = day;
            this.time = time;
            this.subject = subject;
            this.tutor = tutor;
            this.location = location;
        }

        public String getDay() { return day; }
        public String getTime() { return time; }
        public Subject getSubject() { return subject; }
        public Tutor getTutor() { return tutor; }
        public String getLocation() { return location; }

        public String getDisplay() {
            return String.format("%s %s | %s | Tutor: %s | Loc: %s",
                    day, time, subject.getSubjectName(), (tutor == null ? "TBD" : tutor.getFullName()), location);
        }
    }

    private Student owner;
    private List<TimetableRow> rows;

    public Timetable(Student owner) {
        this.owner = owner;
        this.rows = new ArrayList<>();
    }

 //Assign a subjetct to a student's  time slot
    public void assignSubject(String day, String time, Subject subject, Tutor tutor, String location) {
        TimetableRow row = new TimetableRow(day, time, subject, tutor, location);
        rows.add(row);
        System.out.println("Assigned: " + row.getDisplay());
    }

//Prints Timetable
    public void printTimetable() {
        if (rows.isEmpty()) {
            System.out.println("  Timetable is empty.");
            return;
        }
        for (TimetableRow r : rows) {
            System.out.println("  - " + r.getDisplay());
        }
    }
}
