import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        mainScene = new Scene(new Region(), 800, 600);
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
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25));

        Label title = new Label("Tuition01 Login");
        grid.add(title, 0, 0, 2, 1);

        Label roleLabel = new Label("Role:");
        grid.add(roleLabel, 0, 1);
        
        ComboBox<String> roleSelect = new ComboBox<>();
        roleSelect.getItems().addAll("Student", "Tutor");
        roleSelect.setValue("Student");
        grid.add(roleSelect, 1, 1);

        Label userLabel = new Label("Username:");
        grid.add(userLabel, 0, 2);

        TextField userField = new TextField();
        grid.add(userField, 1, 2);

        Label passLabel = new Label("Password:");
        grid.add(passLabel, 0, 3);

        PasswordField passField = new PasswordField();
        grid.add(passField, 1, 3);

        Button loginBtn = new Button("Login");
        Hyperlink registerLink = new Hyperlink("Create an account");
        
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().addAll(registerLink, loginBtn);
        grid.add(hbBtn, 1, 4);

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

        mainScene.setRoot(grid);
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

        ToolBar toolBar = new ToolBar();
        Button logout = new Button("Logout");
        logout.setOnAction(e -> showLoginScreen());
        toolBar.getItems().add(logout);
        root.setTop(toolBar);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setGridLinesVisible(true);
        
        String[] days = {"Time", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < days.length; i++) {
            Label dayLabel = new Label(" " + days[i] + " ");
            GridPane.setHalignment(dayLabel, javafx.geometry.HPos.CENTER);
            grid.add(dayLabel, i, 0);
        }

        for (int hour = 8; hour <= 18; hour++) {
            Label timeLabel = new Label(" " + String.format("%02d:00", hour) + " ");
            grid.add(timeLabel, 0, hour - 7);
        }

        for (Lesson lesson : lessons) {
            VBox box = new VBox();
            box.setAlignment(Pos.CENTER);
            
            Label subj = new Label(lesson.subject);
            Label tut = new Label(lesson.tutor);
            
            box.getChildren().addAll(subj, tut);
            
            grid.add(box, lesson.day, lesson.startHour - 7);
            GridPane.setRowSpan(box, lesson.duration);
        }

        ScrollPane scrollPane = new ScrollPane(grid);
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
