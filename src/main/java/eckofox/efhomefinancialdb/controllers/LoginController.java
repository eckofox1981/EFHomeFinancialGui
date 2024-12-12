package eckofox.efhomefinancialdb.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;
    @FXML
    private Button registerNewUserButton;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField  newUsernameField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmNewPasswordField;
    @FXML
    private Label msgForUsers;
    @FXML
    private Label msgForNewUsers;


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
        System.out.println("register 2 button pressed");
        System.out.println("new username value: " + newUsernameField.getText());
        System.out.println("new password value: " + newPasswordField.getText());
        System.out.println("new passwordconfirm value: " + confirmNewPasswordField.getText());
        if (!passwordMatchControll4NewUser(newPasswordField.getText(), confirmNewPasswordField.getText())){
            msgForNewUsers.setText("Passwords do not match, please try again.");
        } else {
            msgForNewUsers.setText("");
        }

    }


    //----------------------------------------------------------------------------------------------------------------
    private boolean passwordMatchControll4NewUser(String password, String confirmPassword){
        if (!password.equals(confirmPassword)){
            return false;
        }
        return true;
    }

}