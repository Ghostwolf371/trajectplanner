package org.example.trajectplanner.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private TextField studentnummer;
    
    @FXML
    private PasswordField passwoord;
    
    @FXML
    private Button loginbutton;
    
    @FXML
    private Button cancelbutton;

    @FXML
    protected void handleLogin() {
        // TODO: Implement login logic
        System.out.println("Login attempted with student number: " + studentnummer.getText());
    }

    @FXML
    protected void handleCancel() {
        // TODO: Implement cancel logic
        cancelbutton.getScene().getWindow().hide();
    }
}