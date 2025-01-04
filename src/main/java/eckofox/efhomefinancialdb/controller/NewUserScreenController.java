package eckofox.efhomefinancialdb.controller;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.authservice.LoginService;
import eckofox.efhomefinancialdb.authservice.PasswordEncryption;
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

public class NewUserScreenController {
    private Stage stage;
    private App app;
    private LoginService loginService;
    @FXML
    private TextField newUsernameField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmNewPasswordField;
    @FXML
    private Button okButton;
    @FXML
    private Label msgForNewUsers;
    @FXML
    private Button registerNewUserButton;
    @FXML
    private Button cancelNewUserButton;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;

    /** see LoginScreen ln 46
     */
    public NewUserScreenController(App app) {
        this.app = app;
        this.loginService = new LoginService(app);
    }

    public void initData(Stage stage) {
        this.stage = stage;
    }

    /** sets the action of the register button
     * usernames have to be unique (see DatabaseHandler) and therefore the database is checked for usage (in LoginService).
     * since the password field doesn't show the entry, the user is asked to enter the password twice, if the passwords
     *      don't match, the password is not accepted and user is asked to retype it.
     * if the passwords match they are hashed and stored in the database.
     * The OK is hidden to until the user is hidden until the process is complete (ln 86)  to make sure the user follows
     *      the process and the window does not close until the user clicks it.
     * @param event
     */
    @FXML
    public void setRegisterNewUserButton(javafx.event.ActionEvent event) {
        String username = newUsernameField.getText();
        String firstname = firstNameField.getText();
        String lastname = lastNameField.getText();
        String passwordHash;

        if (loginService.userNameExists(username)) { //if exists -> cancel the process
            return;
        }
        if (passwordMatchControlNewUser(newPasswordField.getText(), confirmNewPasswordField.getText())) {
            passwordHash = PasswordEncryption.passwordEncryption(newPasswordField.getText());
            UUID uuid = UUID.randomUUID();
            User user = new User(app, uuid, username, firstname, lastname, passwordHash);
            CheckingAccount checkingAccount = new CheckingAccount(app, user);
            SavingAccount savingAccount = new SavingAccount(app, user);
            user.saving();
            checkingAccount.saving();
            savingAccount.saving();
            msgForNewUsers.setText("Hello " + username + ", account saved.");
            okButton.setVisible(true);
        } else {
            msgForNewUsers.setText("Passwords didn't match, please try again.");
            newPasswordField.clear();
            confirmNewPasswordField.clear();
        }

    }

    /** opens the login window, used for cancel button usage and after registration is complete.
     * @param event
     */
    @FXML
    public void setCancelNewUserButton(javafx.event.ActionEvent event) {
        try {
            Stage currenStage = (Stage) registerNewUserButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eckofox/efhomefinancialdb/login-screen.fxml"));

            fxmlLoader.setControllerFactory(type -> {
                if (type == LoginScreenController.class) {
                    return new LoginScreenController(app); // To be able to pass the app variable.
                }
                return null;
            });

            Parent root;

            root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.getIcons().add(new Image(Objects.requireNonNull(App.class.getResourceAsStream("/logo.png"))));
            stage.setTitle("EF Home Financial - login");
            stage.setScene(new Scene(root));

            LoginScreenController controller = fxmlLoader.getController();
            controller.initData(stage);

            currenStage.close();
            stage.show();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.getStackTrace();
        }
    }

    /** checks the passwords entered in newPasswordField and  confirmNewPasswordField match.
     * @param password = newPasswordField
     * @param confirmPassword = confirmNewPasswordField
     * @return boolean, if false will trigger an event in setRegisterButton (ln 67)
     */
    private boolean passwordMatchControlNewUser(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            return false;
        }
        return true;
    }
}
