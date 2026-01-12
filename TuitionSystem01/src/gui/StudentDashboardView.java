package gui;
import tuition.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.util.List;

public class StudentDashboardView {

    public Parent getView(Main mainApp, String studentId) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        ToolBar toolbar = new ToolBar();
        Button btnEnroll = new Button("Enroll");
        Button btnTimetable = new Button("Timetable");
        Button btnFees = new Button("Fees");
        Button btnLogout = new Button("Logout");
        toolbar.getItems().addAll(btnEnroll, btnTimetable, btnFees, new Separator(), btnLogout);
        root.setTop(toolbar);

        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER);
        Label welcome = new Label("Welcome, " + studentId);
        welcome.setStyle("-fx-font-size: 18px;");
        centerBox.getChildren().add(welcome);
        root.setCenter(centerBox);

        btnEnroll.setOnAction(e -> showEnrollment(centerBox, studentId));
        btnTimetable.setOnAction(e -> showTimetable(centerBox, studentId));
        btnFees.setOnAction(e -> showFees(centerBox, studentId));
        btnLogout.setOnAction(e -> mainApp.showLogin());

        return root;
    }

    private void showEnrollment(VBox container, String studentId) {
        container.getChildren().clear();
        Label title = new Label("Enroll in Subject");
        title.setStyle("-fx-font-weight: bold;");
        
        ComboBox<String> subjectBox = new ComboBox<>();
        List<Subject> subjects = SimpleDataManager.loadSubjects();
        for(Subject s : subjects) {
            String dayStr = "";
            switch(s.getDay()) {
                case 1: dayStr = "Mon"; break;
                case 2: dayStr = "Tue"; break;
                case 3: dayStr = "Wed"; break;
                case 4: dayStr = "Thu"; break;
                case 5: dayStr = "Fri"; break;
                case 6: dayStr = "Sat"; break;
                case 7: dayStr = "Sun"; break;
            }
            String tutorName = SimpleDataManager.getTutorName(s.getTutorId());
            String display = String.format("%s - %s (Tutor: %s) [%s %02d:00]", 
                s.getSubjectID(), s.getName(), tutorName, dayStr, s.getStartHour());
            subjectBox.getItems().add(display);
        }

        Button confirmBtn = new Button("Confirm");
        Label status = new Label();

        confirmBtn.setOnAction(e -> {
            String val = subjectBox.getValue();
            if(val != null) {
                String subId = val.split(" - ")[0];
                SimpleDataManager.saveEnrollment(studentId, subId);
                status.setText("Enrolled in " + subId);
            }
        });

        container.getChildren().addAll(title, subjectBox, confirmBtn, status);
    }

    private void showTimetable(VBox container, String studentId) {
        container.getChildren().clear();
        Label title = new Label("My Timetable");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #F9F9F9; -fx-border-color: transparent;");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.TOP_CENTER);
        
        String[] days = {"Time", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < days.length; i++) {
            Label dayLabel = new Label(days[i]);
            dayLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #555555;");
            GridPane.setHalignment(dayLabel, javafx.geometry.HPos.CENTER);
            grid.add(dayLabel, i, 0);
        }

        for (int hour = 8; hour <= 18; hour++) {
            Label timeLabel = new Label(String.format("%02d:00", hour));
            timeLabel.setStyle("-fx-text-fill: #777777;");
            timeLabel.setPadding(new Insets(0, 10, 0, 0));
            GridPane.setValignment(timeLabel, javafx.geometry.VPos.TOP);
            grid.add(timeLabel, 0, hour - 7);
            
            for (int d = 1; d <= 7; d++) {
                javafx.scene.shape.Rectangle placeholder = new javafx.scene.shape.Rectangle(100, 50);
                placeholder.setFill(javafx.scene.paint.Color.TRANSPARENT);
                placeholder.setStroke(javafx.scene.paint.Color.web("#E0E0E0"));
                placeholder.getStrokeDashArray().addAll(5d, 5d);
                grid.add(placeholder, d, hour - 7);
            }
        }

        List<Enrollment> enrollments = SimpleDataManager.loadEnrollmentsForStudent(studentId);
        for (Enrollment en : enrollments) {
            Subject s = en.getSubject();
            VBox box = new VBox(2);
            box.setPadding(new Insets(5));
            box.setStyle("-fx-background-color: #E3F2FD; -fx-border-color: #2196F3; -fx-border-width: 0 0 0 4; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 0);");
            box.setPrefWidth(100);
            
            Label subj = new Label(s.getName());
            subj.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");
            subj.setWrapText(true);
            
            Label code = new Label(s.getSubjectID());
            code.setStyle("-fx-font-size: 10px; -fx-text-fill: #666666;");

            box.getChildren().addAll(subj, code);
            
            grid.add(box, s.getDay(), s.getStartHour() - 7);
            GridPane.setRowSpan(box, s.getDuration());
        }

        scrollPane.setContent(grid);
        container.getChildren().addAll(title, scrollPane);
    }

    private void showFees(VBox container, String studentId) {
        container.getChildren().clear();
        Label title = new Label("My Fees");
        title.setStyle("-fx-font-weight: bold;");
        
        List<Enrollment> enrollments = SimpleDataManager.loadEnrollmentsForStudent(studentId);
        double total = 0;
        for(Enrollment en : enrollments) {
            total += en.calculateFee();
        }
        
        Label feeLabel = new Label("Total Monthly Fees: RM " + String.format("%.2f", total));
        feeLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: blue;");
        
        container.getChildren().addAll(title, feeLabel);
    }
}
