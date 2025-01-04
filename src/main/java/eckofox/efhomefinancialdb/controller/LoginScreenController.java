package eckofox.efhomefinancialdb.controller;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.authservice.LoginService;
import eckofox.efhomefinancialdb.user.User;
import eckofox.efhomefinancialdb.user.account.CheckingAccount;
import eckofox.efhomefinancialdb.user.account.SavingAccount;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class LoginScreenController {
    Stage stage;
    private App app;
    LoginService loginService;

    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label msgForUsers;

    /** normally controller don't have parameter dependant constructor but i want to access various
     * variables and objects without the use of static, and therefore I have modified the way the all the controllers
     * are initialized, see App.ln 55 or here @ ln 112.
     * @param app
     */
    public LoginScreenController(App app) {
        this.app = app;
        this.loginService = new LoginService(app);
    }

    public void initData(Stage stage) {
        this.stage = stage;
    }

    /** sets the event of a click of the login button:
     * 1. gathers the info from the fields (could have been included directly in if-statements but I kept in "cleaner)
     * 2. runs it through loginCheck -> username check -> password check -> set active user (sent to App)
     * 3. if all checks pass the main screen opens after welcome message is displayed for 1.5 sec
     *      - if checks fail -> a message is displayed to the user.
     * @param event
     */
    @FXML
    public void setLoginButton(javafx.event.ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (loginCheck(username, password)) {
            msgForUsers.setText("Welcome " + app.getActiveUser().getFirstname() + "!");

            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(e -> openMainScreen());
            pause.play();
        }
    }

    /** opens the new user screen
     * @param event
     */
    @FXML
    public void setRegisterButton(javafx.event.ActionEvent event) {
        try {
            Stage currenStage = (Stage) registerButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eckofox/efhomefinancialdb/newuser-screen.fxml"));

            fxmlLoader.setControllerFactory(type -> {
                if (type == NewUserScreenController.class) {
                    return new NewUserScreenController(app); // To be able to pass the app variable.
                }
                return null;
            });

            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.getIcons().add(new Image(Objects.requireNonNull(App.class.getResourceAsStream("/logo.png"))));
            stage.setTitle("EF Home Financial - register new userId");
            stage.setScene(new Scene(root));

            NewUserScreenController controller = fxmlLoader.getController();
            controller.initData(stage);

            currenStage.close();
            stage.show();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.getStackTrace();
        }
    }

    /**
     * opens the main screen after the login check is validated
     */
    private void openMainScreen() {
        try {
            Stage currenStage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eckofox/efhomefinancialdb/main-screen.fxml"));

            fxmlLoader.setControllerFactory(type -> {
                if (type == MainScreenController.class) {
                    return new MainScreenController(app); // To be able to pass the app variable.
                }
                return null;
            });

            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.getIcons().add(new Image(Objects.requireNonNull(App.class.getResourceAsStream("/logo.png"))));
            stage.setTitle("EF Home Financial");
            stage.setScene(new Scene(root));

            MainScreenController controller = fxmlLoader.getController();
            controller.initData(stage);

            currenStage.close();
            stage.show();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.getStackTrace();
        }
    }

    /**
     * sends the parameters the LoginServices for validation.
     * @param username -> to check if username exists and if compatible with password
     * @param password -> to compare with username password hash in database
     * @return boolean to validate (or not) the login.
     */
    public boolean loginCheck(String username, String password) {
        if (!loginService.userNameExists(username)) {
            msgForUsers.setText("This username doesn't exist.\nPlease check spelling or register.");
            return false;
        }

        if (!loginService.passwordCheck(username, password)) {
            msgForUsers.setText("Password did not match records.");
            return false;
        }

        loginService.setActiveUser(username);
        return true;
    }


}