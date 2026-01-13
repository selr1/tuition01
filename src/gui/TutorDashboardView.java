package gui;
import tuition.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class TutorDashboardView {

    public Parent getView(Main mainApp, String tutorId) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        ToolBar toolbar = new ToolBar();
        Button btnSubjects = new Button("My Subjects");
        Button btnStudents = new Button("My Students");
        Button btnLogout = new Button("Logout");
        toolbar.getItems().addAll(btnSubjects, btnStudents, new Separator(), btnLogout);
        root.setTop(toolbar);

        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER);
        Label welcome = new Label("Welcome Tutor, " + tutorId);
        welcome.setStyle("-fx-font-size: 18px;");
        centerBox.getChildren().add(welcome);
        root.setCenter(centerBox);

        btnSubjects.setOnAction(e -> showSubjects(centerBox, tutorId));

        btnStudents.setOnAction(e -> showStudents(centerBox, tutorId));

        btnLogout.setOnAction(e -> mainApp.showLogin());

        return root;
    }
    private void showSubjects(VBox container, String tutorId) {
        container.getChildren().clear();
        Label title = new Label("My Subjects");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        ListView<String> list = new ListView<>();
        java.util.List<Subject> subjects = SimpleDataManager.loadSubjects();
        for(Subject s : subjects) {
            if(s.getTutorId() != null && s.getTutorId().equals(tutorId)) {
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
                list.getItems().add(String.format("%s - %s [%s %02d:00]", 
                    s.getSubjectID(), s.getName(), dayStr, s.getStartHour())); 
            }
        }
        
        if(list.getItems().isEmpty()) {
            list.getItems().add("No subjects assigned.");
        }
        
        container.getChildren().addAll(title, list);
    }

    private void showStudents(VBox container, String tutorId) {
        container.getChildren().clear();
        Label title = new Label("My Students");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        ListView<String> list = new ListView<>();
        java.util.List<String> entries = SimpleDataManager.fetchTutorStudentEntries(tutorId);
        
        list.getItems().addAll(entries);
        
        if(list.getItems().isEmpty()) {
            list.getItems().add("No students found.");
        }
        
        container.getChildren().addAll(title, list);
    }
}
