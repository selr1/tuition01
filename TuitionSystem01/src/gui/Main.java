package gui;
import tuition.*;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage primaryStage;
    private Scene mainScene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("Tuition System ");
        showLogin();
        primaryStage.show();
    }

    public void showLogin() {
        LoginView view = new LoginView();
        if (mainScene == null) {
            mainScene = new Scene(view.getView(this), 800, 600);
            primaryStage.setScene(mainScene);
        } else {
            mainScene.setRoot(view.getView(this));
        }
    }

    public void showRegistration() {
        StudentRegistrationView view = new StudentRegistrationView();
        mainScene.setRoot(view.getView(this));
    }

    public void showStudentDashboard(String studentId) {
        StudentDashboardView view = new StudentDashboardView();
        mainScene.setRoot(view.getView(this, studentId));
    }

    public void showTutorDashboard(String tutorId) {
        TutorDashboardView view = new TutorDashboardView();
        mainScene.setRoot(view.getView(this, tutorId));
    }
}
