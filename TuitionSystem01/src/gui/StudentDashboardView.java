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

        Button btnTimetable = createNavButton("Timetable");
        Button btnCourses = createNavButton("Courses");
        Button btnFees = createNavButton("Fees");
        Button btnProfile = createNavButton("Profile");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("Logout");
        btnLogout.setStyle("-fx-background-color: transparent; -fx-border-color: #999999; -fx-text-fill: #333333; -fx-border-radius: 4; -fx-cursor: hand;");

        header.getChildren().addAll(brand, new Separator(javafx.geometry.Orientation.VERTICAL), btnTimetable, btnCourses, btnFees, btnProfile, spacer, btnLogout);
        root.setTop(header);

        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));
        
        Label welcome = new Label("Welcome, " + studentId);
        welcome.setFont(Font.font("Segoe UI", 24));
        welcome.setTextFill(Color.web("#333333"));
        centerBox.getChildren().add(welcome);
        
        root.setCenter(centerBox);

        btnCourses.setOnAction(e -> showCourses(centerBox, studentId));
        btnTimetable.setOnAction(e -> showTimetable(centerBox, studentId));
        btnFees.setOnAction(e -> showFees(centerBox, studentId));
        btnProfile.setOnAction(e -> showProfile(centerBox, studentId));
        btnLogout.setOnAction(e -> mainApp.showLogin());

        // Default view
        showTimetable(centerBox, studentId);
        
        return root;
    }

    private void showProfile(VBox content, String studentId) {
        content.getChildren().clear();
        
        Label title = new Label("My Profile");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#333333"));
        
        Student student = (Student) DataManager.getInstance().getUser(studentId);
        if (student == null) return;

        VBox profileCard = new VBox(20);
        profileCard.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 0);");
        profileCard.setMaxWidth(600);

        // --- Account Info Section ---
        Label secAccount = new Label("Account Information");
        secAccount.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        secAccount.setTextFill(Color.web("#2196F3"));
        
        GridPane accountGrid = new GridPane();
        accountGrid.setHgap(15);
        accountGrid.setVgap(10);
        addReadOnlyField(accountGrid, "Student ID:", student.getStudentID(), 0);
        addReadOnlyField(accountGrid, "Username:", student.getUsername(), 1);

        // --- Personal Details Section ---
        Label secPersonal = new Label("Personal Details");
        secPersonal.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        secPersonal.setTextFill(Color.web("#2196F3"));
        
        GridPane personalGrid = new GridPane();
        personalGrid.setHgap(15);
        personalGrid.setVgap(10);
        
        TextField nameField = addEditableField(personalGrid, "Full Name:", student.getFullName(), 0);
        TextField phoneField = addEditableField(personalGrid, "Phone Number:", student.getPhone(), 1);
        TextField parentField = addEditableField(personalGrid, "Parent Contact:", student.getParentContact(), 2);
        
        Label lLevel = new Label("Academic Level:");
        lLevel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        lLevel.setTextFill(Color.web("#555555"));
        
        ComboBox<String> levelBox = new ComboBox<>();
        levelBox.getItems().addAll("Primary 1-3", "Primary 4-6", "Secondary Lower", "Secondary Upper");
        levelBox.setValue(student.getLevel());
        levelBox.setMaxWidth(Double.MAX_VALUE);
        levelBox.setStyle("-fx-background-color: white; -fx-border-color: #CCCCCC; -fx-border-radius: 4;");
        
        personalGrid.add(lLevel, 0, 3);
        personalGrid.add(levelBox, 1, 3);
        GridPane.setHgrow(levelBox, Priority.ALWAYS);

        // --- Security Section ---
        Label secSecurity = new Label("Security");
        secSecurity.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        secSecurity.setTextFill(Color.web("#2196F3"));
        
        GridPane securityGrid = new GridPane();
        securityGrid.setHgap(15);
        securityGrid.setVgap(10);
        PasswordField passField = addPasswordField(securityGrid, "Password:", student.getPassword(), 0);

        // --- Actions ---
        Button saveBtn = new Button("Save Changes");
        saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10 20; -fx-font-size: 14px;");
        
        Label status = new Label();
        status.setFont(Font.font("Segoe UI", 12));

        saveBtn.setOnAction(e -> {
            if (nameField.getText().isEmpty() || passField.getText().isEmpty()) {
                status.setText("Name and Password cannot be empty.");
                status.setTextFill(Color.RED);
                return;
            }

            student.setFullName(nameField.getText());
            student.setPhone(phoneField.getText());
            student.setParentContact(parentField.getText());
            student.setPassword(passField.getText());
            student.setLevel(levelBox.getValue());

            boolean success = DataManager.getInstance().updateUser(student);
            if (success) {
                status.setText("Profile updated successfully!");
                status.setTextFill(Color.GREEN);
            } else {
                status.setText("Failed to update profile.");
                status.setTextFill(Color.RED);
            }
        });
        
        HBox actions = new HBox(15);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.getChildren().addAll(saveBtn, status);

        profileCard.getChildren().addAll(
            secAccount, accountGrid, new Separator(),
            secPersonal, personalGrid, new Separator(),
            secSecurity, securityGrid, new Separator(),
            actions
        );

        content.getChildren().addAll(title, profileCard);
    }
    
    private void addReadOnlyField(GridPane grid, String label, String value, int row) {
        Label l = new Label(label);
        l.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        l.setTextFill(Color.web("#666666"));
        
        Label v = new Label(value);
        v.setFont(Font.font("Segoe UI", 14));
        v.setTextFill(Color.web("#333333"));
        
        grid.add(l, 0, row);
        grid.add(v, 1, row);
    }
    
    private TextField addEditableField(GridPane grid, String label, String value, int row) {
        Label l = new Label(label);
        l.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        l.setTextFill(Color.web("#555555"));
        
        TextField tf = new TextField(value);
        tf.setStyle("-fx-background-color: white; -fx-border-color: #CCCCCC; -fx-border-radius: 4; -fx-padding: 8;");
        
        grid.add(l, 0, row);
        grid.add(tf, 1, row);
        GridPane.setHgrow(tf, Priority.ALWAYS);
        return tf;
    }
    
    private PasswordField addPasswordField(GridPane grid, String label, String value, int row) {
        Label l = new Label(label);
        l.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        l.setTextFill(Color.web("#555555"));
        
        PasswordField pf = new PasswordField();
        pf.setText(value);
        pf.setStyle("-fx-background-color: white; -fx-border-color: #CCCCCC; -fx-border-radius: 4; -fx-padding: 8;");
        
        grid.add(l, 0, row);
        grid.add(pf, 1, row);
        GridPane.setHgrow(pf, Priority.ALWAYS);
        return pf;
    }

    private Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #555555; -fx-font-size: 14px; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #F0F0F0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #555555; -fx-font-size: 14px; -fx-cursor: hand;"));
        return btn;
    }

    private void showCourses(VBox container, String studentId) {
        container.getChildren().clear();
        
        VBox content = new VBox(30);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(20));
        content.setMaxWidth(1000);

        // --- Top Section: Available Subjects ---
        VBox availableSection = new VBox(10);
        Label lblAvailable = new Label("Available Subjects");
        lblAvailable.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lblAvailable.setTextFill(Color.web("#333333"));
        
        TableView<Subject> availTable = new TableView<>();
        availTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        availTable.setPrefHeight(250);
        
        TableColumn<Subject, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getSubjectId()));
        
        TableColumn<Subject, String> nameCol = new TableColumn<>("Subject");
        nameCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getSubjectName()));
        
        TableColumn<Subject, String> tutorCol = new TableColumn<>("Tutor");
        tutorCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTutorDetails()));
        
        TableColumn<Subject, String> feeCol = new TableColumn<>("Fee");
        feeCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(String.format("RM %.2f", cell.getValue().getMonthlyFee())));
        
        TableColumn<Subject, String> schedCol = new TableColumn<>("Schedule");
        schedCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            getDayString(cell.getValue().getDayOfWeek()) + " " + String.format("%02d:00", cell.getValue().getStartTime())
        ));

        TableColumn<Subject, Void> reqActionCol = new TableColumn<>("Action");
        reqActionCol.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Request");
            {
                btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                btn.setOnAction(event -> {
                    Subject s = getTableView().getItems().get(getIndex());
                    handleRequest(s, studentId, container);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        
        availTable.getColumns().addAll(idCol, nameCol, tutorCol, feeCol, schedCol, reqActionCol);
        
        // Populate Available Subjects (Exclude enrolled/pending)
        List<Enrollment> myEnrollments = DataManager.getInstance().getStudentEnrollments(studentId);
        List<String> excludedIds = myEnrollments.stream()
                .filter(e -> !e.getStatus().equals("Rejected")) // Keep rejected so they can re-request if needed? Or maybe not.
                .map(Enrollment::getSubjectId)
                .collect(java.util.stream.Collectors.toList());
        
        List<Subject> allSubjects = DataManager.getInstance().getAllSubjects();
        for (Subject s : allSubjects) {
            if (!excludedIds.contains(s.getSubjectId()) && s.checkVacancy() > 0) {
                availTable.getItems().add(s);
            }
        }
        
        availableSection.getChildren().addAll(lblAvailable, availTable);

        // --- Bottom Section: My Requests ---
        VBox requestSection = new VBox(10);
        Label lblRequests = new Label("My Request Status");
        lblRequests.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lblRequests.setTextFill(Color.web("#333333"));
        
        TableView<Enrollment> reqTable = new TableView<>();
        reqTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        reqTable.setPrefHeight(200);
        
        TableColumn<Enrollment, String> rSubCol = new TableColumn<>("Subject");
        rSubCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue().getSubjectId() + " - " + (cell.getValue().getSubject() != null ? cell.getValue().getSubject().getSubjectName() : "Unknown")
        ));
        
        TableColumn<Enrollment, String> rStatusCol = new TableColumn<>("Status");
        rStatusCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus()));
        rStatusCol.setCellFactory(col -> new TableCell<Enrollment, String>() {
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
        
        TableColumn<Enrollment, Void> rActionCol = new TableColumn<>("Action");
        rActionCol.setCellFactory(col -> new TableCell<Enrollment, Void>() {
            private final Button chatBtn = new Button("Discuss");
            private final Button cancelBtn = new Button("Cancel");
            {
                chatBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand;");
                chatBtn.setOnAction(e -> {
                    Enrollment en = getTableView().getItems().get(getIndex());
                    User user = DataManager.getInstance().getUser(studentId);
                    new ChatDialog(en, user).show();
                    showCourses(container, studentId); // Refresh
                });
                
                cancelBtn.setStyle("-fx-background-color: #FF5252; -fx-text-fill: white; -fx-cursor: hand;");
                cancelBtn.setOnAction(e -> {
                    Enrollment en = getTableView().getItems().get(getIndex());
                    DataManager.getInstance().deleteEnrollment(en.getEnrollmentId());
                    showCourses(container, studentId); // Refresh
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Enrollment en = getTableView().getItems().get(getIndex());
                    if ("Pending".equalsIgnoreCase(en.getStatus()) || "Discussing".equalsIgnoreCase(en.getStatus())) {
                        HBox box = new HBox(5, chatBtn, cancelBtn);
                        setGraphic(box);
                    } else {
                        setGraphic(null); // No action for Approved/Active/Rejected here (Active handled in Timetable)
                    }
                }
            }
        });
        
        reqTable.getColumns().addAll(rSubCol, rStatusCol, rActionCol);
        
        // Populate Requests (Pending/Discussing/Rejected/Approved but not yet Active?)
        // Actually, let's show all non-Active here, or maybe just Pending/Discussing/Rejected.
        // Active/Approved are in Timetable.
        // But user might want to see Approved status before dismissing.
        List<Enrollment> requests = myEnrollments.stream()
                .filter(e -> !e.getStatus().equals("Active")) 
                .collect(java.util.stream.Collectors.toList());
        reqTable.getItems().addAll(requests);
        
        requestSection.getChildren().addAll(lblRequests, reqTable);
        
        content.getChildren().addAll(availableSection, new Separator(), requestSection);
        container.getChildren().add(content);
    }

    private void handleRequest(Subject sub, String studentId, VBox container) {
        Student s = (Student) DataManager.getInstance().getUser(studentId);
        if (s == null || sub == null) return;
        
        // 1. Check Vacancy
        if (sub.checkVacancy() <= 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Class is full.");
            alert.showAndWait();
            return;
        }
        
        // 2. Check Time Conflict
        if (DataManager.getInstance().checkTimeConflict(studentId, sub)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Time clash with another subject.");
            alert.showAndWait();
            return;
        }

        Enrollment en = new Enrollment(s, sub);
        boolean success = DataManager.getInstance().saveEnrollment(en);
        
        if (success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Request sent successfully!");
            alert.showAndWait();
            showCourses(container, studentId); // Refresh
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Request failed.");
            alert.showAndWait();
        }
    }
    
    // Helper for day string
    private String getDayString(int day) {
        switch (day) {
            case 1: return "Mon";
            case 2: return "Tue";
            case 3: return "Wed";
            case 4: return "Thu";
            case 5: return "Fri";
            case 6: return "Sat";
            case 7: return "Sun";
            default: return "Unknown";
        }
    }

    private void showTimetable(VBox container, String studentId) {
        container.getChildren().clear();
        
        VBox content = new VBox(30);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(20));
        content.setMaxWidth(1000);

        // --- Top: Visual Timetable ---
        Label title = new Label("My Timetable");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#333333"));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #F9F9F9; -fx-border-color: transparent;");
        scrollPane.setPrefHeight(400);

        GridPane grid = createTimetableGrid(studentId);
        scrollPane.setContent(grid);
        
        // --- Bottom: Enrolled Subjects List ---
        VBox listSection = new VBox(10);
        Label lblList = new Label("Enrolled Subjects");
        lblList.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lblList.setTextFill(Color.web("#333333"));
        
        TableView<Enrollment> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(200);
        
        TableColumn<Enrollment, String> subCol = new TableColumn<>("Subject");
        subCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue().getSubject() != null ? cell.getValue().getSubject().getSubjectName() : "Unknown"
        ));
        
        TableColumn<Enrollment, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(cell -> {
            Subject s = cell.getValue().getSubject();
            if (s == null) return new javafx.beans.property.SimpleStringProperty("-");
            return new javafx.beans.property.SimpleStringProperty(
                getDayString(s.getDayOfWeek()) + " " + String.format("%02d:00", s.getStartTime())
            );
        });
        
        TableColumn<Enrollment, String> tutorCol = new TableColumn<>("Tutor");
        tutorCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue().getSubject() != null ? cell.getValue().getSubject().getTutorDetails() : "Unknown"
        ));
        
        TableColumn<Enrollment, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Drop");
            {
                btn.setStyle("-fx-background-color: #FF5252; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                btn.setOnAction(event -> {
                    Enrollment e = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to drop " + e.getSubject().getSubjectName() + "?", ButtonType.YES, ButtonType.NO);
                    alert.showAndWait();
                    if (alert.getResult() == ButtonType.YES) {
                        boolean success = DataManager.getInstance().deleteEnrollment(e.getEnrollmentId());
                        if (success) {
                            // Real-time refresh
                            showTimetable(container, studentId);
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        
        table.getColumns().addAll(subCol, timeCol, tutorCol, actionCol);
        
        // Populate with Active/Approved enrollments
        List<Enrollment> activeEnrollments = DataManager.getInstance().getStudentEnrollments(studentId).stream()
                .filter(e -> "Active".equalsIgnoreCase(e.getStatus()) || "Approved".equalsIgnoreCase(e.getStatus()))
                .collect(java.util.stream.Collectors.toList());
        table.getItems().addAll(activeEnrollments);
        
        listSection.getChildren().addAll(lblList, table);
        
        content.getChildren().addAll(title, scrollPane, new Separator(), listSection);
        container.getChildren().add(content);
    }
    
    private GridPane createTimetableGrid(String studentId) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.TOP_CENTER);
        
        // Define Column Constraints
        ColumnConstraints timeCol = new ColumnConstraints();
        timeCol.setMinWidth(60); // Fixed width for time labels
        timeCol.setHalignment(javafx.geometry.HPos.RIGHT);
        
        grid.getColumnConstraints().add(timeCol);
        
        for (int i = 0; i < 7; i++) {
            ColumnConstraints dayCol = new ColumnConstraints();
            dayCol.setMinWidth(120);
            dayCol.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(dayCol);
        }
        
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
            if (!"Active".equalsIgnoreCase(en.getStatus()) && !"Approved".equalsIgnoreCase(en.getStatus())) {
                continue;
            }
            
            Subject s = en.getSubject();
            if (s == null) continue;
            
            int day = en.getDayOfWeek() > 0 ? en.getDayOfWeek() : s.getDayOfWeek();
            int time = en.getStartTime() > 0 ? en.getStartTime() : s.getStartTime();
            int duration = en.getDuration() > 0 ? en.getDuration() : s.getDuration();
            
            if (day == 0 || time == 0) continue; 
            
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
        return grid;
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
            if ("Approved".equalsIgnoreCase(en.getStatus()) || "Active".equalsIgnoreCase(en.getStatus())) {
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
