package org.example.trajectplanner.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class NewPasswordController {
    private static final String API_URL = "https://trajectplannerapi.dulamari.com/students/";
    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    private PasswordField newPassword;

    @FXML
    private PasswordField confirmPassword;

    @FXML
    private Label feedbackLabel;

    @FXML
    private Button cancelButton;
    
    private String studentNumber;
    private String currentApiPassword;

    public void setStudentInfo(String studentNumber, String apiPassword) {
        this.studentNumber = studentNumber;
        this.currentApiPassword = apiPassword;
    }

    @FXML
    public void handleChangePassword(ActionEvent event) {
        // Get the entered passwords
        String newPasswordValue = newPassword.getText();
        String confirmPasswordValue = confirmPassword.getText();

        // Validate password fields
        if (newPasswordValue == null || newPasswordValue.trim().isEmpty()) {
            showFeedback("Please enter a new password", true);
            return;
        }

        if (confirmPasswordValue == null || confirmPasswordValue.trim().isEmpty()) {
            showFeedback("Please confirm your new password", true);
            return;
        }

        // Check if passwords match
        if (newPasswordValue.equals(confirmPasswordValue)) {
            // Update the password via API
            updatePasswordOnServer(newPasswordValue, event);
        } else {
            // Show error if passwords don't match
            showFeedback("Passwords do not match!", true);
        }
    }
    
    private void updatePasswordOnServer(String newPassword, ActionEvent event) {
        try {
            // Format student number for API call
            String formattedStudentNumber = studentNumber.replace("/", "-");
            
            // Create HTTP client
            HttpClient client = HttpClient.newHttpClient();
            
            // For a real implementation, this would be a PUT request to update the password
            // Since we don't have an actual update endpoint, we'll just simulate success
            // In a real app, you would make an API call like this:
            /*
            // Create JSON body with updated password
            ObjectNode requestBody = mapper.createObjectNode();
            requestBody.put("password", newPassword);
            
            // Create HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + formattedStudentNumber))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();
                    
            // Send request
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                  .thenAccept(response -> {
                      if (response.statusCode() == 200) {
                          // Password updated successfully
                          Platform.runLater(() -> {
                              showFeedback("Password successfully changed. Redirecting to login...", false);
                              redirectToLogin(event);
                          });
                      } else {
                          // Error updating password
                          Platform.runLater(() -> {
                              showFeedback("Error updating password. Please try again.", true);
                          });
                      }
                  });
            */
            
            // For now, simulate successful password change
            showFeedback("Password successfully changed. Redirecting to login...", false);
            
            // Redirect to login screen after 2 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        try {
                            navigateToLogin(event);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            
        } catch (Exception e) {
            showFeedback("Error updating password: " + e.getMessage(), true);
        }
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        try {
            navigateToLogin(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showFeedback(String message, boolean isError) {
        if (feedbackLabel != null) {
            feedbackLabel.setText(message);
            feedbackLabel.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
            feedbackLabel.setVisible(true);
        } else {
            // If feedbackLabel is not defined in FXML, show console message instead
            System.out.println((isError ? "ERROR: " : "SUCCESS: ") + message);
        }
    }

    private void navigateToLogin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_scherm.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }
}