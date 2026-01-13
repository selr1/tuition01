package gui;
import tuition.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class StudentRegistrationView {

    public Parent getView(Main mainApp) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25));

        Label header = new Label("Register New Student");
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Label idLabel = new Label("Student ID:");
        TextField idField = new TextField();
        idField.setPromptText("S202601");

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();

        Label userLabel = new Label("Username:");
        TextField userField = new TextField();

        Label passLabel = new Label("Password:");
        PasswordField passField = new PasswordField();

        Label phoneLabel = new Label("Phone:");
        TextField phoneField = new TextField();
        
        Label levelLabel = new Label("Level:");
        ComboBox<String> levelBox = new ComboBox<>();
        levelBox.getItems().addAll("Primary 1-3", "Primary 4-6", "Secondary Lower", "Secondary Upper");

        Label parentLabel = new Label("Parent Contact:");
        TextField parentField = new TextField();

        Button saveBtn = new Button("Register");
        Button backBtn = new Button("Back");
        Label statusLabel = new Label();

        saveBtn.setOnAction(e -> {
            if(idField.getText().isEmpty() || levelBox.getValue() == null || passField.getText().isEmpty() || userField.getText().isEmpty() || nameField.getText().isEmpty()) {
                statusLabel.setText("Missing fields!");
                statusLabel.setStyle("-fx-text-fill: red;");
            } else {
                Student s = new Student(
                    nameField.getText(),
                    userField.getText(),
                    idField.getText(), 
                    phoneField.getText(), 
                    levelBox.getValue(), 
                    parentField.getText()
                );
                SimpleDataManager.saveStudent(s, userField.getText(), passField.getText());
                
                statusLabel.setText("Registered!");
                statusLabel.setStyle("-fx-text-fill: green;");
                idField.clear();
                nameField.clear();
                userField.clear();
                passField.clear();
                phoneField.clear();
                parentField.clear();
            }
        });

        backBtn.setOnAction(e -> mainApp.showLogin());

        grid.add(header, 0, 0, 2, 1);
        grid.add(idLabel, 0, 1); grid.add(idField, 1, 1);
        grid.add(nameLabel, 0, 2); grid.add(nameField, 1, 2);
        grid.add(userLabel, 0, 3); grid.add(userField, 1, 3);
        grid.add(passLabel, 0, 4); grid.add(passField, 1, 4);
        grid.add(phoneLabel, 0, 5); grid.add(phoneField, 1, 5);
        grid.add(levelLabel, 0, 6); grid.add(levelBox, 1, 6);
        grid.add(parentLabel, 0, 7); grid.add(parentField, 1, 7);
        grid.add(backBtn, 0, 8); grid.add(saveBtn, 1, 8);
        grid.add(statusLabel, 1, 9);

        return grid;
    }
}
