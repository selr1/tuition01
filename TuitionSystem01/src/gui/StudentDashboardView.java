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
import java.util.List;

public class StudentDashboardView {

    public Parent getView(Main mainApp, String studentId) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F9F9F9;");

        HBox header = new HBox(20);
        header.setPadding(new Insets(15, 30, 15, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-width: 0 0 1 0;");

        Label brand = new Label("Tuition01");
        brand.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        brand.setTextFill(Color.web("#333333"));

        Button btnRequest = createNavButton("Request Subject");
        Button btnMyRequests = createNavButton("My Requests");
        Button btnTimetable = createNavButton("Timetable");
        Button btnFees = createNavButton("Fees");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("Logout");
        btnLogout.setStyle("-fx-background-color: transparent; -fx-border-color: #999999; -fx-text-fill: #333333; -fx-border-radius: 4; -fx-cursor: hand;");

        header.getChildren().addAll(brand, new Separator(javafx.geometry.Orientation.VERTICAL), btnRequest, btnMyRequests, btnTimetable, btnFees, spacer, btnLogout);
        root.setTop(header);

        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));
        
        Label welcome = new Label("Welcome, " + studentId);
        welcome.setFont(Font.font("Segoe UI", 24));
        welcome.setTextFill(Color.web("#333333"));
        centerBox.getChildren().add(welcome);
        
        root.setCenter(centerBox);

        btnRequest.setOnAction(e -> showRequestSubject(centerBox, studentId));
        btnMyRequests.setOnAction(e -> showMyRequests(centerBox, studentId));
        btnTimetable.setOnAction(e -> showTimetable(centerBox, studentId));
        btnFees.setOnAction(e -> showFees(centerBox, studentId));
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

    private void showRequestSubject(VBox container, String studentId) {
        container.getChildren().clear();
        Label title = new Label("Request Subject");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#333333"));
        
        ComboBox<String> subjectBox = new ComboBox<>();
        subjectBox.setStyle("-fx-background-color: white; -fx-border-color: #CCCCCC; -fx-border-radius: 4; -fx-padding: 5;");
        subjectBox.setMaxWidth(Double.MAX_VALUE);

        List<Subject> subjects = DataManager.getInstance().getAllSubjects();
        List<Subject> enrolledSubjects = DataManager.getInstance().getEnrolledSubjectsForStudent(studentId);
        List<String> enrolledIds = enrolledSubjects.stream().map(Subject::getSubjectId).collect(java.util.stream.Collectors.toList());
        
        for(Subject s : subjects) {
            // Filter out subjects already requested/enrolled
            if (enrolledIds.contains(s.getSubjectId())) continue;
            
            // Also check pending requests? DataManager.getStudentEnrollments()
            boolean pending = DataManager.getInstance().getStudentEnrollments(studentId).stream()
                    .anyMatch(e -> e.getSubjectId().equals(s.getSubjectId()) && !e.getStatus().equals("Rejected"));
            if (pending) continue;

            String tutorName = s.getTutorDetails();
            String display = String.format("%s - %s (Tutor: %s)", 
                s.getSubjectId(), s.getSubjectName(), tutorName);
            subjectBox.getItems().add(display);
        }
        
        if (subjectBox.getItems().isEmpty()) {
            subjectBox.setPromptText("No more subjects available to request.");
            subjectBox.setDisable(true);
        }
        
        Button confirmBtn = new Button("Send Request");
        confirmBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        confirmBtn.setMaxWidth(Double.MAX_VALUE);
        
        Label status = new Label();
        status.setFont(Font.font("Segoe UI", 14));

        confirmBtn.setOnAction(e -> {
            String val = subjectBox.getValue();
            if(val != null) {
                String subId = val.split(" - ")[0];
                
                Student s = (Student) DataManager.getInstance().getUser(studentId);
                Subject sub = DataManager.getInstance().getAllSubjects().stream().filter(subj -> subj.getSubjectId().equals(subId)).findFirst().orElse(null);
                
                if (s != null && sub != null) {
                    Enrollment en = new Enrollment(s, sub);
                    boolean success = DataManager.getInstance().saveEnrollment(en);
                    
                    if (success) {
                        status.setText("Request sent for " + subId);
                        status.setTextFill(Color.GREEN);
                        subjectBox.getSelectionModel().clearSelection();
                        // Refresh list to remove the just-requested subject
                        showRequestSubject(container, studentId);
                    } else {
                        status.setText("Request failed (Duplicate?).");
                        status.setTextFill(Color.RED);
                    }
                } else {
                    status.setText("Error: Student or Subject not found.");
                    status.setTextFill(Color.RED);
                }
            } else {
                status.setText("Please select a subject.");
                status.setTextFill(Color.RED);
            }
        });

        VBox content = new VBox(20);
        content.setAlignment(Pos.TOP_CENTER);
        content.setMaxWidth(800);
        content.setPadding(new Insets(40));
        VBox.setMargin(title, new Insets(0, 0, 20, 0)); // Visual Hierarchy margin
        content.getChildren().addAll(title, subjectBox, confirmBtn, status);
        container.getChildren().add(content);
    }
    
    private void showMyRequests(VBox container, String studentId) {
        container.getChildren().clear();
        
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("My Requests");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#333333"));
        
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2196F3; -fx-cursor: hand; -fx-font-weight: bold;");
        refreshBtn.setOnAction(e -> showMyRequests(container, studentId));
        
        headerBox.getChildren().addAll(title, headerSpacer, refreshBtn);
        
        TableView<Enrollment> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Enrollment, String> subCol = new TableColumn<>("Subject");
        subCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue().getSubjectId() + " - " + (cell.getValue().getSubject() != null ? cell.getValue().getSubject().getSubjectName() : "Unknown")
        ));
        
        TableColumn<Enrollment, String> tutorCol = new TableColumn<>("Tutor");
        tutorCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue().getSubject() != null ? cell.getValue().getSubject().getTutorDetails() : "Unknown"
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
                    if (item.equals("Approved")) setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    else if (item.equals("Rejected")) setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    else setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                }
            }
        });
        
        TableColumn<Enrollment, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<Enrollment, Void>() {
            private final Button btn = new Button("Discuss");
            private final Button dismissBtn = new Button("x");
            {
                btn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand;");
                btn.setOnAction(e -> {
                    Enrollment en = getTableView().getItems().get(getIndex());
                    User user = DataManager.getInstance().getUser(studentId);
                    new ChatDialog(en, user).show();
                    showMyRequests(container, studentId);
                });
                
                dismissBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #999999; -fx-cursor: hand; -fx-font-weight: bold; -fx-border-color: #CCCCCC; -fx-border-radius: 10;");
                dismissBtn.setOnAction(e -> {
                    Enrollment en = getTableView().getItems().get(getIndex());
                    en.setStatus("Active");
                    DataManager.getInstance().updateEnrollmentStatus(en.getEnrollmentId(), "Active");
                    showMyRequests(container, studentId);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Enrollment en = getTableView().getItems().get(getIndex());
                    if ("Approved".equalsIgnoreCase(en.getStatus())) {
                        setGraphic(dismissBtn);
                        Tooltip.install(dismissBtn, new Tooltip("Dismiss to Active (Timetable)"));
                    } else {
                        setGraphic(btn);
                    }
                }
            }
        });
        
        table.getColumns().addAll(subCol, tutorCol, statusCol, actionCol);
        
        List<Enrollment> enrollments = DataManager.getInstance().getStudentEnrollments(studentId);
        table.getItems().addAll(enrollments);
        
        VBox content = new VBox(20);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(40));
        content.setMaxWidth(900); // Updated to 900px as per plan
        VBox.setMargin(headerBox, new Insets(0, 0, 20, 0));
        content.getChildren().addAll(headerBox, table);
        container.getChildren().add(content);
    }

    private void showTimetable(VBox container, String studentId) {
        container.getChildren().clear();
        Label title = new Label("My Timetable");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#333333"));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #F9F9F9; -fx-border-color: transparent;");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.TOP_CENTER);
        
        String[] days = {"Time", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < days.length; i++) {
            Label dayLabel = new Label(days[i]);
            dayLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            dayLabel.setTextFill(Color.web("#555555"));
            GridPane.setHalignment(dayLabel, javafx.geometry.HPos.CENTER);
            grid.add(dayLabel, i, 0);
        }

        for (int hour = 8; hour <= 18; hour++) {
            Label timeLabel = new Label(String.format("%02d:00", hour));
            timeLabel.setTextFill(Color.web("#777777"));
            timeLabel.setPadding(new Insets(0, 10, 0, 0));
            GridPane.setValignment(timeLabel, javafx.geometry.VPos.TOP);
            grid.add(timeLabel, 0, hour - 7);
            
            for (int d = 1; d <= 7; d++) {
                javafx.scene.shape.Rectangle placeholder = new javafx.scene.shape.Rectangle(120, 50);
                placeholder.setFill(Color.TRANSPARENT);
                placeholder.setStroke(Color.web("#E0E0E0"));
                placeholder.getStrokeDashArray().addAll(5d, 5d);
                grid.add(placeholder, d, hour - 7);
            }
        }

        List<Enrollment> enrollments = DataManager.getInstance().getStudentEnrollments(studentId);
        for (Enrollment en : enrollments) {
            // Only show Approved/Active enrollments in timetable
            if (!"Active".equalsIgnoreCase(en.getStatus()) && !"Approved".equalsIgnoreCase(en.getStatus())) {
                continue;
            }
            
            Subject s = en.getSubject();
            if (s == null) continue;
            
            // Use Enrollment schedule if set (non-zero), otherwise fallback to Subject (legacy/default)
            int day = en.getDayOfWeek() > 0 ? en.getDayOfWeek() : s.getDayOfWeek();
            int time = en.getStartTime() > 0 ? en.getStartTime() : s.getStartTime();
            int duration = en.getDuration() > 0 ? en.getDuration() : s.getDuration();
            
            if (day == 0 || time == 0) continue; // Not scheduled yet
            
            VBox box = new VBox(2);
            box.setPadding(new Insets(8));
            box.setStyle("-fx-background-color: white; -fx-border-color: #666666; -fx-border-width: 0 0 0 4; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 0);");
            box.setPrefWidth(120);
            
            Label subj = new Label(s.getSubjectName());
            subj.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            subj.setWrapText(true);
            
            Label code = new Label(s.getSubjectId());
            code.setFont(Font.font("Segoe UI", 11));
            code.setTextFill(Color.web("#666666"));

            box.getChildren().addAll(subj, code);
            
            grid.add(box, day, time - 7);
            GridPane.setRowSpan(box, duration);
        }

        scrollPane.setContent(grid);
        
        VBox content = new VBox(20);
        content.getChildren().addAll(title, scrollPane);
        container.getChildren().add(content);
    }

    private void showFees(VBox container, String studentId) {
        container.getChildren().clear();
        Label title = new Label("My Fees");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#333333"));
        
        List<Enrollment> enrollments = DataManager.getInstance().getStudentEnrollments(studentId);
        double total = DataManager.getInstance().calculateTotalFees(studentId);
        
        VBox card = new VBox(10);
        card.setPadding(new Insets(30)); // Increased internal padding
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0); -fx-background-radius: 4;");
        card.setPrefWidth(500); // Increased width
        card.setPrefHeight(300); // Increased height for breakdown
        card.setAlignment(Pos.TOP_CENTER); // Top align for list
        card.setMaxWidth(Double.MAX_VALUE);

        Label feeLabel = new Label("Fee Breakdown");
        feeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        feeLabel.setTextFill(Color.web("#333333"));
        
        VBox breakdown = new VBox(5);
        breakdown.setAlignment(Pos.TOP_LEFT);
        for(Enrollment en : enrollments) {
            if ("Approved".equalsIgnoreCase(en.getStatus())) {
                HBox row = new HBox();
                Label sl = new Label(en.getSubject().getSubjectName());
                Region sp = new Region();
                HBox.setHgrow(sp, Priority.ALWAYS);
                Label fl = new Label(String.format("RM %.2f", en.calculateFee()));
                row.getChildren().addAll(sl, sp, fl);
                breakdown.getChildren().add(row);
            }
        }
        
        Separator sep = new Separator();
        
        HBox totalRow = new HBox();
        totalRow.setAlignment(Pos.CENTER);
        Label totalTxt = new Label("Total Balance: ");
        totalTxt.setFont(Font.font("Segoe UI", 14));
        Label amountLabel = new Label("RM " + String.format("%.2f", total));
        amountLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32)); // Much larger
        amountLabel.setTextFill(Color.web("#333333"));
        totalRow.getChildren().addAll(totalTxt, amountLabel);
        
        card.getChildren().addAll(feeLabel, breakdown, sep, totalRow);
        
        VBox content = new VBox(20);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(40));
        content.setMaxWidth(800);
        VBox.setMargin(title, new Insets(0, 0, 20, 0)); // Visual Hierarchy margin
        content.getChildren().addAll(title, card);
        container.getChildren().add(content);
    }
}
