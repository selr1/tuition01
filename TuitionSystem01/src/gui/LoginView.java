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

public class LoginView {

    public Parent getView(Main mainApp) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #F5F5F5;");

        VBox card = new VBox(20);
        card.setMaxWidth(400);
        card.setPadding(new Insets(40));
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0); -fx-background-radius: 8;");
        card.setAlignment(Pos.CENTER);

        Label title = new Label("Tuition01 Login");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#333333"));

        Label subtitle = new Label("Welcome back");
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setTextFill(Color.web("#666666"));

        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.setAlignment(Pos.CENTER);

        Label roleLabel = new Label("Login as");
        roleLabel.setFont(Font.font("Segoe UI", 12));
        roleLabel.setTextFill(Color.web("#666666"));

        ComboBox<String> roleSelect = new ComboBox<>();
        roleSelect.getItems().addAll("Student", "Tutor", "Admin");
        roleSelect.setValue("Student");
        roleSelect.setMaxWidth(Double.MAX_VALUE);
        styleControl(roleSelect);

        Label userLabel = new Label("Username");
        userLabel.setFont(Font.font("Segoe UI", 12));
        userLabel.setTextFill(Color.web("#666666"));

        TextField userField = new TextField();
        userField.setPromptText("Enter username");
        styleControl(userField);

        Label passLabel = new Label("Password");
        passLabel.setFont(Font.font("Segoe UI", 12));
        passLabel.setTextFill(Color.web("#666666"));

        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter password");
        styleControl(passField);

        formGrid.add(roleLabel, 0, 0);
        formGrid.add(roleSelect, 1, 0);
        formGrid.add(userLabel, 0, 1);
        formGrid.add(userField, 1, 1);
        formGrid.add(passLabel, 0, 2);
        formGrid.add(passField, 1, 2);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(30);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(70);
        formGrid.getColumnConstraints().addAll(col1, col2);

        Button loginBtn = new Button("Login");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;"));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;"));

        Hyperlink registerLink = new Hyperlink("Create an account");
        registerLink.setTextFill(Color.web("#666666"));
        registerLink.setBorder(Border.EMPTY);
        registerLink.setPadding(new Insets(5, 0, 0, 0));

        loginBtn.setOnAction(e -> {
            String u = userField.getText();
            String p = passField.getText();
            String r = roleSelect.getValue();

            User user = DataManager.getInstance().authenticate(u, p);
            
            if (user != null) {
                // Check if role matches
                if (user != null && user.checkPassword(p)) { // Assuming checkPassword exists and 'p' is the password
                    if (user instanceof Student) {
                        mainApp.showStudentDashboard(user.getUserId());
                    } else if (user instanceof Tutor) {
                        mainApp.showTutorDashboard(user.getUserId());
                    } else if (user instanceof Admin) {
                        mainApp.showAdminDashboard(user.getUserId());
                    } else {
                        // This case handles other user types not explicitly covered
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Login Failed");
                        alert.setHeaderText(null);
                        alert.setContentText("Unsupported user type.");
                        alert.showAndWait();
                    }
                } else {
                    // This block would be reached if authenticate returned a user but checkPassword failed,
                    // which is redundant if authenticate already verifies password.
                    // Or if user is null, but that's handled by the outer if.
                    // Assuming this is for a secondary password check or if authenticate returns user even on wrong password.
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Login Failed");
                    alert.setHeaderText(null);
                    alert.setContentText("Invalid Credentials"); // Changed from "Invalid Role for this user."
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Failed");
                alert.setHeaderText(null);
                alert.setContentText("Invalid Credentials");
                alert.showAndWait();
            }
        });

        registerLink.setOnAction(e -> mainApp.showRegistration());

        card.getChildren().addAll(
            title, subtitle, new Separator(),
            formGrid,
            new Region(), loginBtn, registerLink
        );

        root.getChildren().add(card);

        return root;
    }

    private void styleControl(Control c) {
        c.setStyle("-fx-background-color: white; -fx-border-color: #CCCCCC; -fx-border-radius: 4; -fx-padding: 8;");
    }
}
