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

public class LoginScreenController {
    Stage stage;

    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    public void initData (Stage stage){
        this.stage = stage;
    }

    @FXML
    public void setLoginButton(javafx.event.ActionEvent event) {
        System.out.println("login button pressed");
        System.out.println("username value: " + usernameField.getText());
    }

    @FXML
    public void setRegisterButton(javafx.event.ActionEvent event) {
        try {
            Stage currenStage = (Stage) registerButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eckofox/efhomefinancialdb/newuser-screen.fxml"));
            Parent root1;

            root1 = fxmlLoader.load();

            Stage stage = new Stage();
            stage.getIcons().add(new Image(Objects.requireNonNull(App.class.getResourceAsStream("/logo.png"))));
            stage.setTitle("EF Home Financial - register new user");
            stage.setScene(new Scene(root1));

            NewUserScreenController controller = fxmlLoader.getController();
            controller.initData(stage);

            currenStage.close();
            stage.showAndWait();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.getStackTrace();
        }
    }

    @FXML
    public void setRegisterNewUserButton(javafx.event.ActionEvent event) {


    }




}