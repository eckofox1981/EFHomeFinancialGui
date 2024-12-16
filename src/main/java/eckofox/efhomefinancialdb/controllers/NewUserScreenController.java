package eckofox.efhomefinancialdb.controllers;

import eckofox.efhomefinancialdb.application.App;
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

import java.io.IOException;
import java.util.Objects;

public class NewUserScreenController {
    private Stage stage;
    @FXML
    private TextField newUsernameField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmNewPasswordField;
    @FXML
    private Label msgForUsers;
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

    public void initData(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void setRegisterNewUserButton(javafx.event.ActionEvent event) {
        String newMsg = (newUsernameField.getText() + " " + firstNameField.getText() + " " + lastNameField.getText());
        if (newPasswordField.getText().equals(confirmNewPasswordField.getText())) {
            newMsg += " PASS MATCH";
        } else {
            newMsg += " PASS MISMATCH";
        }
        msgForNewUsers.setText(newMsg);
    }

    @FXML
    public void setCancelNewUserButton(javafx.event.ActionEvent event) {
        try {
            Stage currenStage = (Stage) registerNewUserButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eckofox/efhomefinancialdb/login-screen.fxml"));
            Parent root;

            root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.getIcons().add(new Image(Objects.requireNonNull(App.class.getResourceAsStream("/logo.png"))));
            stage.setTitle("EF Home Financial - login");
            stage.setScene(new Scene(root));

            LoginScreenController controller = fxmlLoader.getController();
            controller.initData(stage);

            currenStage.close();
            stage.showAndWait();
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
}
