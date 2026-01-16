package gui;

import source.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ScheduleDialog {

    private final Enrollment enrollment;

    public ScheduleDialog(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public void show() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Schedule Subject");
        stage.setMinWidth(400);

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");

        Label title = new Label("Set Class Schedule");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        Label subtitle = new Label("For " + enrollment.getSubject().getSubjectName());
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setStyle("-fx-text-fill: #666666;");

        // Day Selection
        ComboBox<String> dayCombo = new ComboBox<>();
        dayCombo.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        dayCombo.setPromptText("Select Day");
        dayCombo.setMaxWidth(Double.MAX_VALUE);
        dayCombo.setStyle("-fx-font-size: 14px; -fx-padding: 10;");

        // Time Selection
        ComboBox<String> timeCombo = new ComboBox<>();
        for (int i = 8; i <= 20; i++) {
            timeCombo.getItems().add(String.format("%02d:00", i));
        }
        timeCombo.setPromptText("Select Start Time");
        timeCombo.setMaxWidth(Double.MAX_VALUE);
        timeCombo.setStyle("-fx-font-size: 14px; -fx-padding: 10;");

        Button confirmBtn = new Button("Confirm & Approve");
        confirmBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand;");
        confirmBtn.setMaxWidth(Double.MAX_VALUE);
        
        confirmBtn.setOnAction(e -> {
            String day = dayCombo.getValue();
            String timeStr = timeCombo.getValue();
            
            if (day == null || timeStr == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please select both day and time.");
                alert.showAndWait();
                return;
            }
            

            
            int dayInt = getDayInt(day);
            int timeInt = Integer.parseInt(timeStr.split(":")[0]);
            
            // Update Enrollment Schedule (Negotiated)
            enrollment.setDayOfWeek(dayInt);
            enrollment.setStartTime(timeInt);
            enrollment.setDuration(enrollment.getSubject().getDuration()); // Copy duration from subject default
            enrollment.setStatus("Approved");
            
            // Save Enrollment
            boolean success = DataManager.getInstance().updateEnrollment(enrollment);
            
            if (success) {
                // Notify
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Schedule set and request approved!");
                successAlert.showAndWait();
                stage.close();
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR, "Failed to save enrollment.");
                error.showAndWait();
            }
            

            
            stage.close();
        });

        root.getChildren().addAll(title, subtitle, new Label("Day"), dayCombo, new Label("Start Time"), timeCombo, confirmBtn);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    private int getDayInt(String day) {
        switch(day) {
            case "Monday": return 1;
            case "Tuesday": return 2;
            case "Wednesday": return 3;
            case "Thursday": return 4;
            case "Friday": return 5;
            case "Saturday": return 6;
            case "Sunday": return 7;
            default: return 1;
        }
    }
}
