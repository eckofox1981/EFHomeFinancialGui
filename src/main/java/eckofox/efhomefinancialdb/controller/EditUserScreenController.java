package eckofox.efhomefinancialdb.controller;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.authservice.LoginService;
import eckofox.efhomefinancialdb.authservice.PasswordEncryption;
import eckofox.efhomefinancialdb.user.account.Account;
import eu.hansolo.tilesfx.skins.TestTileSkin;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class EditUserScreenController {
    private App app;
    private Stage stage;
    private LoginService loginService;

    public EditUserScreenController(App app) {
        this.app = app;
        this.loginService = new LoginService(app);
    }

    public void initData(Stage stage) {
        this.stage = stage;
        initializeLeftPane();
    }

    @FXML
    private Label userNameLabel;
    @FXML
    private Label realNameLabel;
    @FXML
    private Button editNameButton;
    @FXML
    private Button changePassButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button exitButton;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private Label nameMsgBox;
    @FXML
    private PasswordField oldPassField;
    @FXML
    private PasswordField newPassField;
    @FXML
    private PasswordField confirmPassField;
    @FXML
    private Label passMsgBox;

    private void initializeLeftPane () {
        app.getActiveUser().getAcountList().forEach(Account::fetchData);
        app.getTransactionManager().gatherAllTransactions();
        userNameLabel.setText("- " + app.getActiveUser().getUsername() + " -");
        realNameLabel.setText(app.getActiveUser().getFirstname() + " " + app.getActiveUser().getLastname());
    }

    @FXML
    private void editName () {
        if ((!app.getActiveUser().getUsername().equals(usernameField.getText()) || !usernameField.getText().isEmpty())
        && loginService.userNameExists(usernameField.getText())) {
            nameMsgBox.setText("Username already being used. Please choose another one.");
            return;
        }
        if (!usernameField.getText().isBlank()) {
            app.getActiveUser().setUsername(usernameField.getText());
        }
        if (!firstNameField.getText().isBlank()) {
            app.getActiveUser().setFirstname(firstNameField.getText());
        }
        if (!lastNameField.getText().isBlank()) {
            app.getActiveUser().setLastname(lastNameField.getText());
        }
        app.getActiveUser().insertData();
        initializeLeftPane();
        nameMsgBox.setTextFill(Color.BLACK);
        nameMsgBox.setText("User details updated.");
    }

    @FXML
    private void changePassword () {
        if (!loginService.passwordCheck(app.getActiveUser().getUsername(), oldPassField.getText())) {
            passMsgBox.setTextFill(Color.ORANGE);
            passMsgBox.setText("Old password not recognised, please retype.");
            return;
        }
        if (!newPassField.getText().equals(confirmPassField.getText())) {
            passMsgBox.setTextFill(Color.ORANGE);
            passMsgBox.setText("New passwords did not match. Please retype.");
            return;
        }
        app.getActiveUser().setPasswordHash(PasswordEncryption.passwordEncryption(newPassField.getText()));

        app.getActiveUser().insertData();
        passMsgBox.setTextFill(Color.BLACK);
        passMsgBox.setText("Password updated.");
    }

    @FXML
    private void setExitButton () {
        try {
            Stage currenStage = (Stage) exitButton.getScene().getWindow();
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

    @FXML
    private void setDeleteButton() {
        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
        deleteAlert.setTitle("Delete user");
        deleteAlert.setContentText("You are about to delete the user - " + app.getActiveUser().getUsername() + " -.\n" +
                "Do you wish to continue?\nThis cannot be undone.");
        Optional<ButtonType> result = deleteAlert.showAndWait();

        if (result.isEmpty()) {
        } else if (result.get() == ButtonType.OK) {
            app.getActiveUser().deleteData();
            openLoginScreen();
        } else if (result.get() == ButtonType.CANCEL) {
            usernameField.setText("User not deleted.");
        }
    }

    private void openLoginScreen () {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/eckofox/efhomefinancialdb/login-screen.fxml"));

            fxmlLoader.setControllerFactory(type -> {
                if (type == LoginScreenController.class) {
                    return new LoginScreenController(app);
                }
                return null;
            });

            stage.getIcons().add(new Image(Objects.requireNonNull(App.class.getResourceAsStream("/logo.png"))));
            Scene scene = new Scene(fxmlLoader.load());

            stage.setTitle("Welcome to EF Home Financial");

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

}
