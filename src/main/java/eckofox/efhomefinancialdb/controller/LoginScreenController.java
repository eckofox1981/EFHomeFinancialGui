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

    public LoginScreenController(App app) {
        this.app = app;
        this.loginService = new LoginService(app);

    }

    public void initData (Stage stage){
        this.stage = stage;
    }

    @FXML
    public void setLoginButton(javafx.event.ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (loginCheck(username, password)) {
            msgForUsers.setText("Welcome " + app.getActiveUser().getFirstname() + "!");

            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> openMainScreen());
            pause.play();

        }
        //nothing
    }

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

    public void setApp(App app) {
        this.app = app;
    }

    public App getApp() {
        return app;
    }

    public boolean loginCheck(String username, String password){
        if (!loginService.userNameExists(username)){
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