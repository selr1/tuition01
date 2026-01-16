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

        Button btnTimetable = createNavButton("Timetable");
        Button btnRequests = createNavButton("Requests");
        Button btnEarnings = createNavButton("My Earnings");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("Logout");
        btnLogout.setStyle("-fx-background-color: transparent; -fx-border-color: #999999; -fx-text-fill: #333333; -fx-border-radius: 4; -fx-cursor: hand;");
        btnLogout.setOnAction(e -> mainApp.showLogin());

        header.getChildren().addAll(brand, new Separator(javafx.geometry.Orientation.VERTICAL), btnTimetable, btnRequests, btnEarnings, spacer, btnLogout);
        root.setTop(header);

        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));
        
        Label welcome = new Label("Welcome Tutor, " + tutorId);
        welcome.setFont(Font.font("Segoe UI", 24));
        welcome.setTextFill(Color.web("#333333"));
        centerBox.getChildren().add(welcome);
        
        root.setCenter(centerBox);

        btnTimetable.setOnAction(e -> showTimetable(centerBox, tutorId));
        btnRequests.setOnAction(e -> showRequests(centerBox, tutorId));
        btnEarnings.setOnAction(e -> showEarnings(centerBox, tutorId));
      
        showTimetable(centerBox, tutorId);

        return root;
    }

    private Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #555555; -fx-font-size: 14px; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #F0F0F0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #555555; -fx-font-size: 14px; -fx-cursor: hand;"));
        return btn;
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

    private void showEarnings(VBox container, String tutorId) {
        container.getChildren().clear();
        
        Label title = new Label("My Earnings");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#333333"));
        
        // Calculate Total Earnings
        java.util.List<Enrollment> students = DataManager.getInstance().getTutorStudents(tutorId);
        double totalEarnings = students.stream().mapToDouble(Enrollment::calculateFee).sum();
        
        // Total Earnings Card
        VBox totalCard = new VBox(10);
        totalCard.setAlignment(Pos.CENTER);
        totalCard.setPadding(new Insets(20));
        totalCard.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);");
        totalCard.setMaxWidth(300);
        
        Label totalLabel = new Label("Total Monthly Earnings");
        totalLabel.setTextFill(Color.WHITE);
        totalLabel.setFont(Font.font("Segoe UI", 16));
        
        Label amountLabel = new Label(String.format("RM %.2f", totalEarnings));
        amountLabel.setTextFill(Color.WHITE);
        amountLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        
        totalCard.getChildren().addAll(totalLabel, amountLabel);
        
        // Breakdown by Subject
        Label breakdownTitle = new Label("Breakdown by Subject");
        breakdownTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        breakdownTitle.setTextFill(Color.web("#555555"));
        
        VBox breakdownList = new VBox(10);
        
        java.util.Map<Subject, java.util.List<Enrollment>> bySubject = students.stream()
            .collect(java.util.stream.Collectors.groupingBy(Enrollment::getSubject));
            
        for (Subject s : bySubject.keySet()) {
            HBox row = new HBox(15);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(15));
            row.setStyle("-fx-background-color: white; -fx-background-radius: 4; -fx-border-color: #E0E0E0; -fx-border-radius: 4;");
            
            VBox info = new VBox(5);
            Label subName = new Label(s.getSubjectName() + " (" + s.getSubjectId() + ")");
            subName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            
            int count = bySubject.get(s).size();
            Label subDetail = new Label(count + " Student" + (count > 1 ? "s" : "") + " × RM" + s.getMonthlyFee());
            subDetail.setTextFill(Color.GRAY);
            
            info.getChildren().addAll(subName, subDetail);
            
            Region r = new Region();
            HBox.setHgrow(r, Priority.ALWAYS);
            
            double subTotal = count * s.getMonthlyFee();
            Label subAmount = new Label(String.format("RM %.2f", subTotal));
            subAmount.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
            subAmount.setTextFill(Color.web("#333333"));
            
            row.getChildren().addAll(info, r, subAmount);
            breakdownList.getChildren().add(row);
        }
        
        if (bySubject.isEmpty()) {
            breakdownList.getChildren().add(new Label("No active classes to calculate earnings."));
        }
        
        ScrollPane scroll = new ScrollPane(breakdownList);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        
        VBox content = new VBox(20);
        content.getChildren().addAll(title, totalCard, breakdownTitle, scroll);
        content.setMaxWidth(900);
        VBox.setVgrow(content, Priority.ALWAYS);
        
        container.getChildren().add(content);
    }
    private void showTimetable(VBox container, String tutorId) {
        container.getChildren().clear();
        
        VBox content = new VBox(30);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(20));
        content.setMaxWidth(1000);

        // --- Top: Visual Timetable ---
        Label title = new Label("My Teaching Schedule");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#333333"));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #F9F9F9; -fx-border-color: transparent;");
        scrollPane.setPrefHeight(400);

        GridPane grid = createTimetableGrid(tutorId);
        scrollPane.setContent(grid);
        
        // --- Bottom: My Classes List ---
        VBox classListSection = new VBox(15);
        classListSection.setPadding(new Insets(10));
        
        Label listTitle = new Label("My Classes");
        listTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        listTitle.setTextFill(Color.web("#333333"));
        classListSection.getChildren().add(listTitle);
        
        // Get all approved/active enrollments for this tutor
        java.util.List<Enrollment> students = DataManager.getInstance().getTutorStudents(tutorId);
        
        // Group by Subject + Schedule
        java.util.Map<String, java.util.List<Enrollment>> grouped = students.stream()
            .collect(java.util.stream.Collectors.groupingBy(e -> {
                Subject s = e.getSubject();
                // CHECK ENROLLMENT OVERRIDE FIRST
                int day = e.getDayOfWeek() > 0 ? e.getDayOfWeek() : s.getDayOfWeek();
                int time = e.getStartTime() > 0 ? e.getStartTime() : s.getStartTime();
                String dayStr = getDayString(day);
                return String.format("%s - %s [%s %02d:00]", s.getSubjectId(), s.getSubjectName(), dayStr, time);
            }));
            
        if (grouped.isEmpty()) {
            Label emptyLabel = new Label("No active classes yet. Approve requests to see them here.");
            emptyLabel.setFont(Font.font("Segoe UI", 14));
            emptyLabel.setTextFill(Color.GRAY);
            classListSection.getChildren().add(emptyLabel);
        }
        
        for (String key : grouped.keySet()) {
            TitledPane pane = new TitledPane();
            pane.setText(key + " (" + grouped.get(key).size() + " students)");
            pane.setExpanded(true); 
            pane.setStyle("-fx-font-size: 14px;");
            
            VBox paneContent = new VBox(10);
            paneContent.setPadding(new Insets(10));
            
            for (Enrollment en : grouped.get(key)) {
                HBox studentRow = new HBox(10);
                studentRow.setAlignment(Pos.CENTER_LEFT);
                studentRow.setStyle("-fx-background-color: #F5F5F5; -fx-padding: 10; -fx-background-radius: 4;");
                
                Label nameLabel = new Label(en.getStudent() != null ? en.getStudent().getFullName() : en.getStudentId());
                nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
                nameLabel.setTextFill(Color.web("#333333"));
                
                Label idLabel = new Label("(" + en.getStudentId() + ")");
                idLabel.setTextFill(Color.GRAY);
                
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                
                Button chatBtn = new Button("Chat");
                chatBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand;");
                chatBtn.setOnAction(e -> {
                    User user = DataManager.getInstance().getUser(tutorId);
                    new ChatDialog(en, user).show();
                });
                
                Button profileBtn = new Button("Profile");
                profileBtn.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-cursor: hand;");
                profileBtn.setOnAction(e -> {
                    if (en.getStudent() != null) {
                        new StudentDetailsDialog(en.getStudent(), en).show();
                    }
                });
                
                Button removeBtn = new Button("Remove");
                removeBtn.setStyle("-fx-background-color: #FF5252; -fx-text-fill: white; -fx-cursor: hand;");
                removeBtn.setOnAction(e -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Remove " + nameLabel.getText() + " from class?", ButtonType.YES, ButtonType.NO);
                    alert.showAndWait();
                    if (alert.getResult() == ButtonType.YES) {
                        boolean success = DataManager.getInstance().deleteEnrollment(en.getEnrollmentId());
                        if (success) {
                            showTimetable(container, tutorId); // Real-time refresh
                        }
                    }
                });
                
                studentRow.getChildren().addAll(nameLabel, idLabel, spacer, chatBtn, profileBtn, removeBtn);
                paneContent.getChildren().add(studentRow);
            }
            pane.setContent(paneContent);
            classListSection.getChildren().add(pane);
        }
        
        content.getChildren().addAll(title, scrollPane, new Separator(), classListSection);
        container.getChildren().add(content);
    }

    private GridPane createTimetableGrid(String tutorId) {
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

        // Get all active/approved enrollments for this tutor
        java.util.List<Enrollment> enrollments = DataManager.getInstance().getTutorStudents(tutorId);
        java.util.Set<String> processedSlots = new java.util.HashSet<>();
        
        for (Enrollment en : enrollments) {
            Subject s = en.getSubject();
            if (s == null) continue;
            
            // CHECK ENROLLMENT OVERRIDE FIRST
            int day = en.getDayOfWeek() > 0 ? en.getDayOfWeek() : s.getDayOfWeek();
            int time = en.getStartTime() > 0 ? en.getStartTime() : s.getStartTime();
            int duration = en.getDuration() > 0 ? en.getDuration() : s.getDuration();
            
            if (day == 0 || time == 0) continue; 
            
            // Unique key for subject+time to avoid stacking same class multiple times
            // We use SubjectID + Day + Time because the same subject might be taught at different times now
            String key = s.getSubjectId() + "-" + day + "-" + time;
            if (processedSlots.contains(key)) continue;
            processedSlots.add(key);
            
            VBox box = new VBox(2);
            box.setPadding(new Insets(8));
            box.setStyle("-fx-background-color: white; -fx-border-color: #4CAF50; -fx-border-width: 0 0 0 4; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 0);");
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
}
