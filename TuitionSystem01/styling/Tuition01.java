import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author zha
 */

public class Tuition01 extends Application {

    private Stage primaryStage;
    private Scene mainScene; 
    private List<User> users = new ArrayList<>();
    private List<Lesson> lessons = new ArrayList<>();

    private final String USER_FILE = "/home/zha/users.json";
    private final String TIMETABLE_FILE = "/home/zha/timetable.json";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        
        loadData();

        mainScene = new Scene(new Region(), 1024, 768);
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Tuition01 System");
        
        showLoginScreen();
        
        primaryStage.show();
    }

    private void loadData() {
        try {
            String userContent = new String(Files.readAllBytes(Paths.get(USER_FILE)));
            parseUsers(userContent);

            String timetableContent = new String(Files.readAllBytes(Paths.get(TIMETABLE_FILE)));
            parseLessons(timetableContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseUsers(String json) {
        String[] objects = json.split("\\}");
        for (String obj : objects) {
            if (!obj.contains("username")) continue;
            String u = extractRegex(obj, "username");
            String p = extractRegex(obj, "password");
            String r = extractRegex(obj, "role");
            users.add(new User(u, p, r));
        }
    }

    private void parseLessons(String json) {
        String[] objects = json.split("\\}");
        for (String obj : objects) {
            if (!obj.contains("subject")) continue;
            int day = Integer.parseInt(extractRegex(obj, "day"));
            int start = Integer.parseInt(extractRegex(obj, "startHour"));
            int duration = Integer.parseInt(extractRegex(obj, "duration"));
            String subject = extractRegex(obj, "subject");
            String tutor = extractRegex(obj, "tutor");
            lessons.add(new Lesson(day, start, duration, subject, tutor));
        }
    }

    private String extractRegex(String source, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"?([^,\"}]+)\"?");
        Matcher matcher = pattern.matcher(source);
        return matcher.find() ? matcher.group(1).trim() : "";
    }

    private void showLoginScreen() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #F5F5F5;");

        VBox card = new VBox(15);
        card.setMaxWidth(350);
        card.setPadding(new Insets(40));
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0); -fx-background-radius: 8;");
        card.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Tuition01");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#333333"));

        Label subtitle = new Label("Welcome back");
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setTextFill(Color.web("#666666"));

        ComboBox<String> roleSelect = new ComboBox<>();
        roleSelect.getItems().addAll("Student", "Tutor");
        roleSelect.setValue("Student");
        roleSelect.setMaxWidth(Double.MAX_VALUE);
        styleControl(roleSelect);

        TextField userField = new TextField();
        userField.setPromptText("Username");
        styleControl(userField);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        styleControl(passField);

        Button loginBtn = new Button("Login");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;"));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;"));

        Hyperlink registerLink = new Hyperlink("Create an account");
        registerLink.setTextFill(Color.web("#666666"));
        registerLink.setOnAction(e -> showAlert("Coming Soon", "Registration is done by other member."));

        loginBtn.setOnAction(e -> {
            if ("Tutor".equals(roleSelect.getValue())) {
                showAlert("Coming Soon", "Tutor login is done by other member.");
                return;
            }
            if (authenticate(userField.getText(), passField.getText(), "student")) {
                showMainScreen();
            } else {
                showAlert("Login Failed", "Invalid credentials.");
            }
        });

        card.getChildren().addAll(title, subtitle, new Separator(), new Label("Login as"), roleSelect, new Label("Username"), userField, new Label("Password"), passField, new Region(), loginBtn, registerLink);
        root.getChildren().add(card);

        mainScene.setRoot(root);
    }

    private void styleControl(Control c) {
        c.setStyle("-fx-background-color: white; -fx-border-color: #CCCCCC; -fx-border-radius: 4; -fx-padding: 8;");
    }

    private boolean authenticate(String user, String pass, String role) {
        for (User u : users) {
            if (u.username.equals(user) && u.password.equals(pass) && u.role.equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }

    private void showMainScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F9F9F9;");

        HBox header = new HBox(20);
        header.setPadding(new Insets(15, 30, 15, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-width: 0 0 1 0;");

        Label brand = new Label("Tuition01");
        brand.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button logout = new Button("Logout");
        logout.setStyle("-fx-background-color: transparent; -fx-border-color: #999999; -fx-text-fill: #333333; -fx-border-radius: 4;");

        logout.setOnAction(e -> showLoginScreen());

        header.getChildren().addAll(brand, spacer, logout);
        root.setTop(header);

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
                Rectangle placeholder = new Rectangle(120, 50);
                placeholder.setFill(Color.TRANSPARENT);
                placeholder.setStroke(Color.web("#E0E0E0"));
                placeholder.getStrokeDashArray().addAll(5d, 5d);
                grid.add(placeholder, d, hour - 7);
            }
        }

        for (Lesson lesson : lessons) {
            VBox box = new VBox(2);
            box.setPadding(new Insets(8));
            box.setStyle("-fx-background-color: white; -fx-border-color: #666666; -fx-border-width: 0 0 0 4; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 0);");
            box.setPrefWidth(120);
            
            Label subj = new Label(lesson.subject);
            subj.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            subj.setWrapText(true);
            
            Label tut = new Label(lesson.tutor);
            tut.setFont(Font.font("Segoe UI", 11));
            tut.setTextFill(Color.web("#666666"));

            box.getChildren().addAll(subj, tut);
            
            grid.add(box, lesson.day, lesson.startHour - 7);
            GridPane.setRowSpan(box, lesson.duration);
        }

        scrollPane.setContent(grid);
        root.setCenter(scrollPane);

        mainScene.setRoot(root);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    static class User {
        String username, password, role;
        public User(String u, String p, String r) { username = u; password = p; role = r; }
    }

    static class Lesson {
        int day, startHour, duration;
        String subject, tutor;
        public Lesson(int d, int s, int dur, String sub, String t) {
            day = d; startHour = s; duration = dur; subject = sub; tutor = t;
        }
    }
}