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

public class TutorDashboardView {

    public Parent getView(Main mainApp, String tutorId) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F9F9F9;");

        HBox header = new HBox(20);
        header.setPadding(new Insets(15, 30, 15, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-width: 0 0 1 0;");

        Label brand = new Label("Tuition01");
        brand.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        brand.setTextFill(Color.web("#333333"));

        Button btnClasses = createNavButton("My Classes");
        Button btnRequests = createNavButton("Requests");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("Logout");
        btnLogout.setStyle("-fx-background-color: transparent; -fx-border-color: #999999; -fx-text-fill: #333333; -fx-border-radius: 4; -fx-cursor: hand;");

        header.getChildren().addAll(brand, new Separator(javafx.geometry.Orientation.VERTICAL), btnClasses, btnRequests, spacer, btnLogout);
        root.setTop(header);

        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));
        
        Label welcome = new Label("Welcome Tutor, " + tutorId);
        welcome.setFont(Font.font("Segoe UI", 24));
        welcome.setTextFill(Color.web("#333333"));
        centerBox.getChildren().add(welcome);
        
        root.setCenter(centerBox);

        btnClasses.setOnAction(e -> showClasses(centerBox, tutorId));
        btnRequests.setOnAction(e -> showRequests(centerBox, tutorId));
        btnLogout.setOnAction(e -> mainApp.showLogin());

        return root;
    }

    private Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #555555; -fx-font-size: 14px; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #F0F0F0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #555555; -fx-font-size: 14px; -fx-cursor: hand;"));
        return btn;
    }

    private void showClasses(VBox container, String tutorId) {
        container.getChildren().clear();
        Label title = new Label("My Classes");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#333333"));
        
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        VBox classList = new VBox(15);
        classList.setPadding(new Insets(10));
        
        // Get all approved/active enrollments for this tutor
        java.util.List<Enrollment> students = DataManager.getInstance().getTutorStudents(tutorId);
        
        // Group by Subject + Schedule
        java.util.Map<String, java.util.List<Enrollment>> grouped = students.stream()
            .collect(java.util.stream.Collectors.groupingBy(e -> {
                Subject s = e.getSubject();
                int day = e.getDayOfWeek() > 0 ? e.getDayOfWeek() : s.getDayOfWeek();
                int time = e.getStartTime() > 0 ? e.getStartTime() : s.getStartTime();
                String dayStr = getDayString(day);
                return String.format("%s - %s [%s %02d:00]", s.getSubjectId(), s.getSubjectName(), dayStr, time);
            }));
            
        if (grouped.isEmpty()) {
            classList.getChildren().add(new Label("No active classes yet."));
        }
        
        for (String key : grouped.keySet()) {
            TitledPane pane = new TitledPane();
            pane.setText(key + " (" + grouped.get(key).size() + " students)");
            pane.setExpanded(false);
            
            VBox content = new VBox(5);
            for (Enrollment en : grouped.get(key)) {
                Label l = new Label("• " + (en.getStudent() != null ? en.getStudent().getFullName() : en.getStudentId()));
                content.getChildren().add(l);
            }
            pane.setContent(content);
            classList.getChildren().add(pane);
        }
        
        scroll.setContent(classList);
        
        VBox content = new VBox(20);
        content.getChildren().addAll(title, scroll);
        content.setMaxWidth(900);
        container.getChildren().add(content);
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
    
    private void showRequests(VBox container, String tutorId) {
        container.getChildren().clear();
        
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("Incoming Requests");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#333333"));
        
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2196F3; -fx-cursor: hand; -fx-font-weight: bold;");
        refreshBtn.setOnAction(e -> showRequests(container, tutorId));
        
        headerBox.getChildren().addAll(title, refreshBtn);
        
        TableView<Enrollment> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Enrollment, String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue().getStudent() != null ? cell.getValue().getStudent().getFullName() : cell.getValue().getStudentId()
        ));
        studentCol.setStyle("-fx-font-weight: bold;");
        
        TableColumn<Enrollment, String> subjectCol = new TableColumn<>("Subject");
        subjectCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue().getSubject() != null ? cell.getValue().getSubject().getSubjectName() : cell.getValue().getSubjectId()
        ));
        
        TableColumn<Enrollment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus()));
        statusCol.setCellFactory(col -> new TableCell<Enrollment, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                }
            }
        });
        
        TableColumn<Enrollment, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<Enrollment, Void>() {
            private final Button chatBtn = new Button("Chat");
            private final Button approveBtn = new Button("Approve");
            private final HBox pane = new HBox(5, chatBtn, approveBtn);
            
            {
                pane.setAlignment(Pos.CENTER);
                
                chatBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 12px;");
                chatBtn.setOnAction(e -> {
                    Enrollment en = getTableView().getItems().get(getIndex());
                    User user = DataManager.getInstance().getUser(tutorId);
                    new ChatDialog(en, user).show();
                });
                
                approveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 12px;");
                approveBtn.setOnAction(e -> {
                    Enrollment en = getTableView().getItems().get(getIndex());
                    new ScheduleDialog(en).show();
                    // Note: ScheduleDialog handles the status update and saving
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
        
        table.getColumns().addAll(studentCol, subjectCol, statusCol, actionCol);
        
        java.util.List<Enrollment> enrollments = DataManager.getInstance().getTutorRequests(tutorId);
        table.getItems().addAll(enrollments);
        
        VBox content = new VBox(20);
        content.getChildren().addAll(headerBox, table);
        content.setMaxWidth(900);
        container.getChildren().add(content);
    }
}
