package eckofox.efhomefinancialdb.controllers;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.user.User;
import eckofox.efhomefinancialdb.user.account.CheckingAccount;
import eckofox.efhomefinancialdb.user.account.SavingAccount;
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
    }

    public void initData (Stage stage){
        this.stage = stage;
    }

    @FXML
    public void setLoginButton(javafx.event.ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (loginCheck(username, password)) {
            openMainScreen();
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

    private boolean loginCheck(String username, String password){
        if (!userNameExists(username)){
            msgForUsers.setText("This username doesn't exist.\nPlease check spelling or register.");
            return false;
        }

        if (!passwordCheck(username, password)) {
            msgForUsers.setText("Password did not match records.");
            return false;
        }

        setActiveUser(username);
        return true;
    }

    private boolean userNameExists (String username) {
        try (PreparedStatement usernameIsPresentStatement =
                     app.getConnection().prepareStatement("SELECT username FROM users WHERE username = ?;")) {
            usernameIsPresentStatement.setString(1, username);
            try (ResultSet result = usernameIsPresentStatement.executeQuery()) {
                while (result.next()) {
                    if (result.getString("username").equals(username)) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Unable to query in checkUsernameUsage. " + e.getMessage());
        }
        return false;
    }

    private boolean passwordCheck (String username, String password) {
        String passwordHash = "";

        try (PreparedStatement checkPasswordHashStatement =
                     app.getConnection().prepareStatement("SELECT passwordhash FROM users WHERE username =?")){
            checkPasswordHashStatement.setString(1, username);

            try (ResultSet result = checkPasswordHashStatement.executeQuery()){
                if (result.next()) {
                    passwordHash = result.getString("passwordhash");
                }
            } catch (SQLException e) {
                System.err.println("Unable to execute query at passworCheck. " + e.getMessage());
            }
        } catch (SQLException sqlException) {
            System.err.println("Error with prepared statement checkpasswordCheck. " + sqlException.getMessage());
        }

        if (BCrypt.checkpw(password, passwordHash)) {
            System.out.println("Login succesfull");
            msgForUsers.setText("Logged in.");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Nah, it'll be fine");
            }
            return true;
        }

        return false;
    }

    private void setActiveUser(String username){
        User user = new User();

        try (PreparedStatement selectAllStatement = app.getConnection().prepareStatement("SELECT * FROM users WHERE username = ?;")){
            selectAllStatement.setString(1, username);
            try (ResultSet result = selectAllStatement.executeQuery()) {
                while (result.next()) {
                    user = userFromResult(result);
                    app.setActiveUser(user);
                    app.getActiveUser().getAcountList().add(new CheckingAccount(app, user));
                    app.getActiveUser().getAcountList().add(new SavingAccount(app, user));
                }
            } catch (SQLException e) {
                System.err.println("Error while creating userId at setActiveUser. " + e.getMessage());
            }

        }catch (SQLException sqlException) {
            System.err.println("Error with prepared statement in setActiveUser. " + sqlException.getMessage());
        }
    }

    private User userFromResult (ResultSet result) throws SQLException {
        UUID userid = UUID.fromString(result.getString("userid"));
        String username = result.getString("username");
        String firstname = result.getString("firstname");
        String lastname = result.getString("lastname");

        return new User(app, userid, username, firstname, lastname);
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
}