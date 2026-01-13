package gui;
import tuition.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class LoginView {

    public Parent getView(Main mainApp) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25));

        Label title = new Label("Tuition01 Login");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        grid.add(title, 0, 0, 2, 1);

        Label roleLabel = new Label("Role:");
        grid.add(roleLabel, 0, 1);
        
        ComboBox<String> roleSelect = new ComboBox<>();
        roleSelect.getItems().addAll("Student", "Tutor");
        roleSelect.setValue("Student");
        grid.add(roleSelect, 1, 1);

        Label userLabel = new Label("Username or ID:");
        grid.add(userLabel, 0, 2);

        TextField userField = new TextField();
        grid.add(userField, 1, 2);

        Label passLabel = new Label("Password:");
        grid.add(passLabel, 0, 3);

        PasswordField passField = new PasswordField();
        grid.add(passField, 1, 3);

        Button loginBtn = new Button("Login");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(loginBtn);
        grid.add(hbBtn, 1, 4);

        Hyperlink registerLink = new Hyperlink("Create an account");
        grid.add(registerLink, 1, 5);

        loginBtn.setOnAction(e -> {
            String u = userField.getText();
            String p = passField.getText();
            String r = roleSelect.getValue();

            String sessionId = SimpleDataManager.authenticate(u, p, r);
            if (sessionId != null) {
                if (r.equalsIgnoreCase("Student")) {
                    mainApp.showStudentDashboard(sessionId);
                } else {
                    mainApp.showTutorDashboard(sessionId);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Invalid Credentials");
                alert.showAndWait();
            }
        });

        registerLink.setOnAction(e -> mainApp.showRegistration());

        return grid;
    }
}
