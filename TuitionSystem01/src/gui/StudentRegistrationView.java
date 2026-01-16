package gui;
import source.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class StudentRegistrationView {

    public Parent getView(Main mainApp) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #F5F5F5;");

        VBox card = new VBox(20);
        card.setMaxWidth(500);
        card.setPadding(new Insets(40));
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0); -fx-background-radius: 8;");
        card.setAlignment(Pos.CENTER);

        Label header = new Label("Register New Student");
        header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        header.setTextFill(Color.web("#333333"));

        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.setAlignment(Pos.CENTER);

        Label idLabel = createLabel("Student ID");
        TextField idField = new TextField();
        idField.setPromptText("S202601");
        styleControl(idField);

        Label nameLabel = createLabel("Name");
        TextField nameField = new TextField();
        styleControl(nameField);

        Label userLabel = createLabel("Username");
        TextField userField = new TextField();
        styleControl(userField);

        Label passLabel = createLabel("Password");
        PasswordField passField = new PasswordField();
        styleControl(passField);

        Label phoneLabel = createLabel("Phone");
        TextField phoneField = new TextField();
        styleControl(phoneField);
        
        Label levelLabel = createLabel("Level");
        ComboBox<String> levelBox = new ComboBox<>();
        levelBox.getItems().addAll("Primary 1-3", "Primary 4-6", "Secondary Lower", "Secondary Upper");
        levelBox.setMaxWidth(Double.MAX_VALUE);
        styleControl(levelBox);

        Label parentLabel = createLabel("Parent Contact");
        TextField parentField = new TextField();
        styleControl(parentField);

        formGrid.add(idLabel, 0, 0);
        formGrid.add(idField, 1, 0);
        formGrid.add(nameLabel, 0, 1);
        formGrid.add(nameField, 1, 1);
        formGrid.add(userLabel, 0, 2);
        formGrid.add(userField, 1, 2);
        formGrid.add(passLabel, 0, 3);
        formGrid.add(passField, 1, 3);
        formGrid.add(phoneLabel, 0, 4);
        formGrid.add(phoneField, 1, 4);
        formGrid.add(levelLabel, 0, 5);
        formGrid.add(levelBox, 1, 5);
        formGrid.add(parentLabel, 0, 6);
        formGrid.add(parentField, 1, 6);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(30);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(70);
        formGrid.getColumnConstraints().addAll(col1, col2);

        Button saveBtn = new Button("Register");
        saveBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        
        Button backBtn = new Button("Back to Login");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #666666; -fx-cursor: hand;");
        backBtn.setMaxWidth(Double.MAX_VALUE);

        Label statusLabel = new Label();
        statusLabel.setFont(Font.font("Segoe UI", 12));

        saveBtn.setOnAction(e -> {
            if(idField.getText().isEmpty() || levelBox.getValue() == null || passField.getText().isEmpty() || userField.getText().isEmpty() || nameField.getText().isEmpty()) {
                statusLabel.setText("Missing fields!");
                statusLabel.setTextFill(Color.RED);
            } else {
                Student s = new Student(
                    nameField.getText(),
                    userField.getText(),
                    passField.getText(),
                    idField.getText(), 
                    phoneField.getText(), 
                    levelBox.getValue(), 
                    parentField.getText()
                );
                boolean success = DataManager.getInstance().saveUser(s);
                
                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Registration Successful");
                    alert.setHeaderText(null);
                    alert.setContentText("Account created! Redirecting to login...");
                    alert.showAndWait();
                    mainApp.showLogin();
                } else {
                    statusLabel.setText("Registration Failed. Try again.");
                    statusLabel.setTextFill(Color.RED);
                }
            }
        });

        backBtn.setOnAction(e -> mainApp.showLogin());

        card.getChildren().addAll(
            header, new Separator(),
            formGrid,
            new Region(), saveBtn, backBtn, statusLabel
        );

        // Wrapper to center the card within the ScrollPane
        StackPane contentWrapper = new StackPane(card);
        contentWrapper.setAlignment(Pos.CENTER);
        contentWrapper.setPadding(new Insets(20));
        contentWrapper.setStyle("-fx-background-color: transparent;");

        ScrollPane scroll = new ScrollPane(contentWrapper);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true); // Ensures content wrapper fills the viewport height for centering
        scroll.setStyle("-fx-background: #F5F5F5; -fx-border-color: transparent; -fx-background-color: transparent;");
        
        root.getChildren().add(scroll);

        return root;
    }

    private Label createLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Segoe UI", 12));
        l.setTextFill(Color.web("#666666"));
        return l;
    }

    private void styleControl(Control c) {
        c.setStyle("-fx-background-color: white; -fx-border-color: #CCCCCC; -fx-border-radius: 4; -fx-padding: 8;");
    }
}
