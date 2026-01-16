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
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChatDialog {

    private final Enrollment enrollment;
    private final User currentUser;
    private final boolean isTutor;
    private VBox messageContainer;
    private ScrollPane scrollPane;

    public ChatDialog(Enrollment enrollment, User currentUser) {
        this.enrollment = enrollment;
        this.currentUser = currentUser;
        this.isTutor = currentUser instanceof Tutor;
    }

    public void show() {
        // Security Check: Ensure current user is authorized to view this enrollment
        boolean authorized = false;
        if (currentUser instanceof Student) {
            authorized = currentUser.getUserId().equals(enrollment.getStudentId());
        } else if (currentUser instanceof Tutor) {
            // Check if tutor owns the subject
            if (enrollment.getSubject() != null) {
                authorized = currentUser.getUserId().equals(enrollment.getSubject().getTutorId());
            }
        }
        
        if (!authorized) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unauthorized access to this discussion.");
            alert.showAndWait();
            return;
        }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Discussion - " + (enrollment.getSubject() != null ? enrollment.getSubject().getSubjectName() : enrollment.getSubjectId()));
        stage.setMinWidth(500);
        stage.setMinHeight(600);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F9F9F9;");

        // Header
        HBox header = new HBox(15);
        header.setPadding(new Insets(15));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-width: 0 0 1 0;");

        Label title = new Label("Discussion with " + (isTutor ? enrollment.getStudent().getFullName() : enrollment.getTutorName()));
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(title, spacer);
        
        if (isTutor && "Discussing".equals(enrollment.getStatus())) {
            Button acceptBtn = new Button("Accept Request");
            acceptBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            acceptBtn.setOnAction(e -> {
                new ScheduleDialog(enrollment).show();
                stage.close();
            });
            header.getChildren().add(acceptBtn);
        }

        root.setTop(header);

        // Messages Area
        messageContainer = new VBox(10);
        messageContainer.setPadding(new Insets(15));
        messageContainer.setStyle("-fx-background-color: #F9F9F9;");
        
        scrollPane = new ScrollPane(messageContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        loadMessages();
        root.setCenter(scrollPane);

        // Input Area
        HBox inputArea = new HBox(10);
        inputArea.setPadding(new Insets(15));
        inputArea.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-width: 1 0 0 0;");
        inputArea.setAlignment(Pos.CENTER_LEFT);

        TextField inputField = new TextField();
        inputField.setPromptText("Type a message...");
        HBox.setHgrow(inputField, Priority.ALWAYS);
        inputField.setStyle("-fx-padding: 10; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #CCCCCC;");

        Button sendBtn = new Button("Send");
        sendBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand;");
        sendBtn.setMinWidth(80);
        
        sendBtn.setOnAction(e -> sendMessage(inputField));
        inputField.setOnAction(e -> sendMessage(inputField));

        inputArea.getChildren().addAll(inputField, sendBtn);
        root.setBottom(inputArea);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void loadMessages() {
        messageContainer.getChildren().clear();
        List<Message> messages = DataManager.getInstance().getMessagesForEnrollment(enrollment.getEnrollmentId());
        
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm");

        for (Message msg : messages) {
            boolean isMe = msg.getSenderId().equals(currentUser.getUserId()) || 
                          (isTutor && "Tutor".equals(msg.getSenderId())) || // Legacy check
                          (!isTutor && "Student".equals(msg.getSenderId())); // Legacy check

            VBox bubble = new VBox(5);
            bubble.setMaxWidth(350);
            bubble.setPadding(new Insets(10));
            
            Label sender = new Label(msg.getSenderName());
            sender.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
            sender.setTextFill(isMe ? Color.WHITE : Color.GRAY);
            
            Label content = new Label(msg.getContent());
            content.setWrapText(true);
            content.setFont(Font.font("Segoe UI", 14));
            content.setTextFill(isMe ? Color.WHITE : Color.BLACK);
            
            Label time = new Label(msg.getTimestamp().format(timeFormatter));
            time.setFont(Font.font("Segoe UI", 9));
            time.setTextFill(isMe ? Color.web("#E0E0E0") : Color.GRAY);
            time.setAlignment(Pos.BOTTOM_RIGHT);

            bubble.getChildren().addAll(sender, content, time);

            if (isMe) {
                bubble.setStyle("-fx-background-color: #2196F3; -fx-background-radius: 15 15 0 15;");
                HBox row = new HBox(bubble);
                row.setAlignment(Pos.CENTER_RIGHT);
                messageContainer.getChildren().add(row);
            } else {
                bubble.setStyle("-fx-background-color: white; -fx-background-radius: 15 15 15 0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");
                HBox row = new HBox(bubble);
                row.setAlignment(Pos.CENTER_LEFT);
                messageContainer.getChildren().add(row);
            }
        }
        
        // Scroll to bottom
        scrollPane.setVvalue(1.0);
    }

    private void sendMessage(TextField inputField) {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        Message msg = new Message(
            enrollment.getEnrollmentId(),
            currentUser.getUserId(), // Use actual ID
            currentUser.getFullName(),
            text
        );
        
        DataManager.getInstance().saveMessage(msg);
        
        // If status is Pending, update to Discussing
        if ("Pending".equals(enrollment.getStatus())) {
            DataManager.getInstance().updateEnrollmentStatus(enrollment.getEnrollmentId(), "Discussing");
        }
        
        inputField.clear();
        loadMessages();
    }
}
