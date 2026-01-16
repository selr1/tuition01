package gui;

import source.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.List;

public class StudentDetailsDialog {

    private final Student student;
    private final Enrollment currentEnrollment;

    public StudentDetailsDialog(Student student, Enrollment currentEnrollment) {
        this.student = student;
        this.currentEnrollment = currentEnrollment;
    }

    public void show() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Student Details: " + student.getFullName());

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Tab 1: Profile
        Tab profileTab = new Tab("Profile");
        profileTab.setContent(createProfileView());

        // Tab 2: Enrollments
        Tab enrollmentsTab = new Tab("Other Enrollments");
        enrollmentsTab.setContent(createEnrollmentsView());

        // Tab 3: Progress
        Tab progressTab = new Tab("Progress Report");
        progressTab.setContent(createProgressView(stage));

        tabPane.getTabs().addAll(profileTab, enrollmentsTab, progressTab);

        Scene scene = new Scene(tabPane, 500, 400);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private VBox createProfileView() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.TOP_LEFT);

        addDetail(box, "Full Name", student.getFullName());
        addDetail(box, "Student ID", student.getStudentID());
        addDetail(box, "Academic Level", student.getLevel() != null ? student.getLevel() : "Not specified");
        addDetail(box, "Phone Number", student.getPhone() != null ? student.getPhone() : "Not specified");
        addDetail(box, "Parent Contact", student.getParentContact() != null ? student.getParentContact() : "Not specified");

        return box;
    }

    private void addDetail(VBox box, String label, String value) {
        VBox row = new VBox(2);
        Label l = new Label(label);
        l.setTextFill(Color.GRAY);
        l.setFont(Font.font("Segoe UI", 12));
        
        Label v = new Label(value);
        v.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        
        box.getChildren().addAll(l, v);
    }

    private VBox createEnrollmentsView() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(20));

        Label title = new Label("All Enrolled Subjects");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        box.getChildren().add(title);

        List<Enrollment> enrollments = DataManager.getInstance().getStudentEnrollments(student.getStudentID());
        
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        VBox list = new VBox(10);
        list.setPadding(new Insets(5));
        
        if (enrollments.isEmpty()) {
            list.getChildren().add(new Label("No enrollments found."));
        } else {
            for (Enrollment en : enrollments) {
                HBox row = new HBox(10);
                row.setStyle("-fx-background-color: #F5F5F5; -fx-padding: 10; -fx-background-radius: 4;");
                row.setAlignment(Pos.CENTER_LEFT);
                
                VBox info = new VBox(2);
                Label sub = new Label(en.getSubject() != null ? en.getSubject().getSubjectName() : en.getSubjectId());
                sub.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
                
                Label tutor = new Label("Tutor: " + (en.getSubject() != null ? en.getSubject().getTutorDetails() : "Unknown"));
                tutor.setTextFill(Color.GRAY);
                
                info.getChildren().addAll(sub, tutor);
                
                Region r = new Region();
                HBox.setHgrow(r, Priority.ALWAYS);
                
                Label status = new Label(en.getStatus());
                if ("Active".equalsIgnoreCase(en.getStatus()) || "Approved".equalsIgnoreCase(en.getStatus())) {
                    status.setTextFill(Color.GREEN);
                } else {
                    status.setTextFill(Color.ORANGE);
                }
                status.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
                
                row.getChildren().addAll(info, r, status);
                list.getChildren().add(row);
            }
        }
        
        scroll.setContent(list);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        box.getChildren().add(scroll);
        
        return box;
    }

    private VBox createProgressView(Stage stage) {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));

        Label title = new Label("Progress Report for " + (currentEnrollment.getSubject() != null ? currentEnrollment.getSubject().getSubjectName() : "this subject"));
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        TextArea progressArea = new TextArea();
        progressArea.setPromptText("Enter progress notes, observations, or feedback here...");
        progressArea.setWrapText(true);
        if (currentEnrollment.getProgress() != null) {
            progressArea.setText(currentEnrollment.getProgress());
        }
        VBox.setVgrow(progressArea, Priority.ALWAYS);

        Button saveBtn = new Button("Save Progress");
        saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        
        saveBtn.setOnAction(e -> {
            currentEnrollment.setProgress(progressArea.getText());
            boolean success = DataManager.getInstance().updateEnrollment(currentEnrollment);
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Progress saved successfully.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save progress.");
                alert.showAndWait();
            }
        });

        box.getChildren().addAll(title, progressArea, saveBtn);
        return box;
    }
}
