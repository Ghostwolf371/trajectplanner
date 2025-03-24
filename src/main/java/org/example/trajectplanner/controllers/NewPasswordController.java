package org.example.trajectplanner.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;

public class NewPasswordController {
    @FXML
    private PasswordField newPassword;

    @FXML
    protected void handleChangePassword() {
        // TODO: Implement password change logic
        System.out.println("Password change attempted");
    }

    @FXML
    protected void handleCancel() {
        // Close the window
        newPassword.getScene().getWindow().hide();
    }
}