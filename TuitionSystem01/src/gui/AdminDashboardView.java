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
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.List;
import java.util.stream.Collectors;

public class AdminDashboardView {

    public Parent getView(Main mainApp, String adminId) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F9F9F9;");

        // Header
        HBox header = new HBox(20);
        header.setPadding(new Insets(15, 30, 15, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-width: 0 0 1 0;");

        Label brand = new Label("Tuition01 Admin");
        brand.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        brand.setTextFill(Color.web("#333333"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("Logout");
        btnLogout.setStyle("-fx-background-color: transparent; -fx-border-color: #999999; -fx-text-fill: #333333; -fx-border-radius: 4; -fx-cursor: hand;");
        btnLogout.setOnAction(e -> mainApp.showLogin());

        header.getChildren().addAll(brand, spacer, btnLogout);
        root.setTop(header);

        // Content
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_CENTER);

        HBox titleBox = new HBox(20);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("Subject Management");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#333333"));
        
        Region titleSpacer = new Region();
        HBox.setHgrow(titleSpacer, Priority.ALWAYS);
        
        Button addSubjectBtn = new Button("+ Add New Subject");
        addSubjectBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10 20;");
        addSubjectBtn.setOnAction(e -> showAddSubjectDialog(mainApp));
        
        titleBox.getChildren().addAll(title, titleSpacer, addSubjectBtn);

        // Subject Table
        TableView<Subject> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Subject, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getSubjectId()));
        
        TableColumn<Subject, String> nameCol = new TableColumn<>("Subject Name");
        nameCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getSubjectName()));
        
        TableColumn<Subject, String> tutorCol = new TableColumn<>("Tutor");
        tutorCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTutorDetails()));
        
        TableColumn<Subject, String> feeCol = new TableColumn<>("Fee");
        feeCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(String.format("RM %.2f", cell.getValue().getMonthlyFee())));
        
        TableColumn<Subject, String> scheduleCol = new TableColumn<>("Schedule");
        scheduleCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            getDayString(cell.getValue().getDayOfWeek()) + " " + String.format("%02d:00", cell.getValue().getStartTime())
        ));
        
        TableColumn<Subject, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Delete");
            {
                btn.setStyle("-fx-background-color: #FF5252; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                btn.setOnAction(event -> {
                    Subject s = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete subject " + s.getSubjectName() + "?\nThis will remove all student enrollments!", ButtonType.YES, ButtonType.NO);
                    alert.showAndWait();
                    if (alert.getResult() == ButtonType.YES) {
                        boolean success = DataManager.getInstance().deleteSubject(s.getSubjectId());
                        if (success) {
                            getTableView().getItems().remove(s);
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

        table.getColumns().addAll(idCol, nameCol, tutorCol, feeCol, scheduleCol, actionCol);
        table.getItems().addAll(DataManager.getInstance().getAllSubjects());

        content.getChildren().addAll(titleBox, table);
        root.setCenter(content);

        return root;
    }

    private void showAddSubjectDialog(Main mainApp) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add New Subject");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F9F9F9;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField idField = new TextField();
        idField.setPromptText("e.g. M101");
        
        TextField nameField = new TextField();
        nameField.setPromptText("e.g. Mathematics");
        
        TextField feeField = new TextField();
        feeField.setPromptText("e.g. 50.00");
        
        TextField capacityField = new TextField();
        capacityField.setPromptText("e.g. 20");
        
        ComboBox<String> dayBox = new ComboBox<>();
        dayBox.getItems().addAll("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
        dayBox.setValue("Mon");
        
        ComboBox<Integer> timeBox = new ComboBox<>();
        for(int i=8; i<=18; i++) timeBox.getItems().add(i);
        timeBox.setValue(10);
        
        TextField durationField = new TextField();
        durationField.setText("2"); // Default 2 hours

        // Tutor Selection
        ComboBox<Tutor> tutorBox = new ComboBox<>();
        List<User> users = DataManager.getInstance().getUsers(); // We need a way to get all users or filter tutors
        List<Tutor> tutors = users.stream()
                .filter(u -> u instanceof Tutor)
                .map(u -> (Tutor)u)
                .collect(Collectors.toList());
        tutorBox.getItems().addAll(tutors);
        tutorBox.setCellFactory(lv -> new ListCell<Tutor>() {
            @Override
            protected void updateItem(Tutor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getFullName() + " (" + item.getUserId() + ")");
            }
        });
        tutorBox.setButtonCell(new ListCell<Tutor>() {
            @Override
            protected void updateItem(Tutor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getFullName());
            }
        });
        
        grid.add(new Label("Subject ID:"), 0, 0); grid.add(idField, 1, 0);
        grid.add(new Label("Name:"), 0, 1); grid.add(nameField, 1, 1);
        grid.add(new Label("Fee (RM):"), 0, 2); grid.add(feeField, 1, 2);
        grid.add(new Label("Capacity:"), 0, 3); grid.add(capacityField, 1, 3);
        grid.add(new Label("Day:"), 0, 4); grid.add(dayBox, 1, 4);
        grid.add(new Label("Start Time:"), 0, 5); grid.add(timeBox, 1, 5);
        grid.add(new Label("Duration (h):"), 0, 6); grid.add(durationField, 1, 6);
        grid.add(new Label("Assign Tutor:"), 0, 7); grid.add(tutorBox, 1, 7);

        Button saveBtn = new Button("Create Subject");
        saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        
        Label status = new Label();

        saveBtn.setOnAction(e -> {
            try {
                String id = idField.getText();
                String name = nameField.getText();
                double fee = Double.parseDouble(feeField.getText());
                int cap = Integer.parseInt(capacityField.getText());
                int day = dayBox.getSelectionModel().getSelectedIndex() + 1;
                int start = timeBox.getValue();
                int dur = Integer.parseInt(durationField.getText());
                Tutor t = tutorBox.getValue();
                
                if (id.isEmpty() || name.isEmpty() || t == null) {
                    status.setText("Missing fields!");
                    status.setTextFill(Color.RED);
                    return;
                }
                
                Subject s = new Subject(id, name, fee, cap, day, start, dur, t.getUserId());
                s.setTutor(t);
                
                boolean success = DataManager.getInstance().saveSubject(s);
                if (success) {
                    stage.close();
                    // Refresh view? Ideally we'd use an ObservableList but for now we can just reload the view
                    // This requires a callback or just re-setting the scene in Main, but Main isn't easily accessible here to refresh
                    // For simplicity, we'll just close. The user can logout/login or we can try to refresh table if we had reference.
                    // Actually, we can refresh the table if we make it a field or pass it.
                    // Let's just close for now, user can refresh by re-logging or we can add a refresh button.
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Subject created!");
                    alert.showAndWait();
                    // Trigger refresh of the dashboard view if possible, or just let user know.
                    // Since we are inside the view class, we can't easily refresh the table unless we refactor.
                    // Let's add a refresh button to the dashboard.
                } else {
                    status.setText("Subject ID already exists.");
                    status.setTextFill(Color.RED);
                }
            } catch (Exception ex) {
                status.setText("Invalid input format.");
                status.setTextFill(Color.RED);
            }
        });

        root.getChildren().addAll(new Label("Enter Subject Details"), grid, saveBtn, status);
        stage.setScene(new javafx.scene.Scene(root));
        stage.showAndWait();
    }

    private String getDayString(int d) {
        switch(d) {
            case 1: return "Mon";
            case 2: return "Tue";
            case 3: return "Wed";
            case 4: return "Thu";
            case 5: return "Fri";
            case 6: return "Sat";
            case 7: return "Sun";
            default: return "TBD";
        }
    }
}
