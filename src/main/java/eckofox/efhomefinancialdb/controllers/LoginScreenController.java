package eckofox.efhomefinancialdb.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginScreenController {
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;



    @FXML
    public void setLoginButton(javafx.event.ActionEvent event) {
        System.out.println("login button pressed");
        System.out.println("username value: " + usernameField.getText());
    }

    @FXML
    public void setRegisterButton(javafx.event.ActionEvent event) {
        System.out.println("register button pressed");
        System.out.println("password value: " + passwordField.getText());
    }

    @FXML
    public void setRegisterNewUserButton(javafx.event.ActionEvent event) {


    }




}