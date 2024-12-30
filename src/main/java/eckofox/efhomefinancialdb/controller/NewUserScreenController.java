package eckofox.efhomefinancialdb.controller;

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

public class NewUserScreenController {
    private Stage stage;
    private App app;
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

    public NewUserScreenController(App app) {
        this.app = app;
    }

    public void initData(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void setRegisterNewUserButton(javafx.event.ActionEvent event) {
        String username = newUsernameField.getText();
        String firstname = firstNameField.getText();
        String lastname = lastNameField.getText();
        String passwordHash;

        if (checkUsernameUsage(username)){
            return;
        }
        if (passwordMatchControlNewUser(newPasswordField.getText(), confirmNewPasswordField.getText())){
            passwordHash = passwordEncryption(newPasswordField.getText());
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



    //----------------------------------------------------------------------------------------------------------------
    private boolean passwordMatchControlNewUser(String password, String confirmPassword){
        if (!password.equals(confirmPassword)){
            return false;
        }
        return true;
    }

    private String passwordEncryption (String confirmedPassword){
        return BCrypt.hashpw(confirmedPassword, BCrypt.gensalt());
    }

    private boolean checkUsernameUsage (String username){
        try{
            app.getDataBaseHandler().connectToDatabase();
        } catch (SQLException e){
            System.err.println("Could not connect to database in checkUsernameUsage. " + e.getMessage());
        }
        try (PreparedStatement usernameIsUsedStatement =
                app.getConnection().prepareStatement("SELECT username FROM users WHERE username = ?;")){
            usernameIsUsedStatement.setString(1, username);
            try (ResultSet result = usernameIsUsedStatement.executeQuery()) {
                while (result.next()) {
                    if (result.getString("username").equals(username)){
                        msgForNewUsers.setText("The username '" + username + "' is already in used.");
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Unable to query in checkUsernameUsage. " + e.getMessage());
        }

        return false;
    }
}
