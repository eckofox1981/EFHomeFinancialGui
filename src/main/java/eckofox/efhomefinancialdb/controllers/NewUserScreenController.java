package eckofox.efhomefinancialdb.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class NewUserScreenController {

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




    //----------------------------------------------------------------------------------------------------------------
    private boolean passwordMatchControlNewUser(String password, String confirmPassword){
        if (!password.equals(confirmPassword)){
            return false;
        }
        return true;
    }
}
