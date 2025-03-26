package org.example.trajectplanner.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LoginController {
    @FXML
    private Button loginbutton;

    private static final String API_URL = "https://trajectplannerapi.dulamari.com/students/";
    private final ObjectMapper mapper = new ObjectMapper();
    
    // Default system password that triggers the password change screen
    private static final String DEFAULT_PASSWORD = "9x#V@7p!Lz$Q2w%T8m^C3j*B6r&K0d";
    
    // For storing the user's student number when redirecting to change password screen
    private static String userStudentNumber;

    // For admin bypass (for testing only)
    private static final String ADMIN_BYPASS = "ADMIN";

    // For admin login
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    @FXML
    private TextField studentnummer;
    
    @FXML
    private Button cancelbutton;

    @FXML
    private PasswordField passwoord;
    
    @FXML
    private Label feedbackLabel;

    @FXML
    public void handleLogin(ActionEvent event) {
        String studentNumber = studentnummer.getText();
        String password = passwoord.getText();

        try {
            // Check if student number field is empty
            if (studentNumber == null || studentNumber.trim().isEmpty()) {
                showFeedback("Please enter your student number", true);
                return;
            }

            // Check if password field is empty
            if (password == null || password.trim().isEmpty()) {
                showFeedback("Please enter your password", true);
                return;
            }
            
            // Check for admin login
            if (studentNumber.equalsIgnoreCase(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
                try {
                    navigateToAdminPage(event);
                    return;
                } catch (IOException e) {
                    showFeedback("Error navigating to admin page: " + e.getMessage(), true);
                    return;
                }
            }
            
            // Admin bypass option (for testing only)
            if (password.equals(ADMIN_BYPASS)) {
                try {
                    navigateToDashboard(event);
                    return;
                } catch (IOException e) {
                    showFeedback("Error navigating to dashboard: " + e.getMessage(), true);
                    return;
                }
            }

            // Format student number for API call
            String formattedStudentNumber = studentNumber.replace("/", "-");
            
            // Create HTTP client and request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + formattedStudentNumber))
                    .build();

            // Send request and handle response
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            try {
                                JsonNode jsonNode = mapper.readTree(response.body());
                                if (jsonNode.isArray() && jsonNode.size() > 0) {
                                    JsonNode student = jsonNode.get(0);
                                    String apiPassword = student.get("password").asText();
                                    
                                    // Verify password
                                    if (password.equals(apiPassword)) {
                                        // Check if the user's password is the default password
                                        if (password.equals(DEFAULT_PASSWORD)) {
                                            // Store student number for password change screen
                                            userStudentNumber = studentNumber;
                                            
                                            // New user with default password, redirect to change password
                                            Platform.runLater(() -> {
                                                try {
                                                    navigateToChangePassword(event, studentNumber);
                                                } catch (IOException e) {
                                                    showFeedback("Error navigating to password change: " + e.getMessage(), true);
                                                }
                                            });
                                        } else {
                                            // Regular login, proceed to main application
                                            Platform.runLater(() -> {
                                                try {
                                                    navigateToDashboard(event);
                                                } catch (IOException e) {
                                                    showFeedback("Error navigating to dashboard: " + e.getMessage(), true);
                                                }
                                            });
                                        }
                                    } else {
                                        Platform.runLater(() -> showFeedback("Invalid password", true));
                                    }
                                } else {
                                    Platform.runLater(() -> showFeedback("Student not found", true));
                                }
                            } catch (Exception e) {
                                Platform.runLater(() -> showFeedback("Error processing response: " + e.getMessage(), true));
                            }
                        } else {
                            Platform.runLater(() -> showFeedback("Error connecting to server", true));
                        }
                    })
                    .exceptionally(e -> {
                        Platform.runLater(() -> showFeedback("Connection error: " + e.getMessage(), true));
                        return null;
                    });
            
        } catch (Exception e) {
            showFeedback("An error occurred: " + e.getMessage(), true);
        }
    }
    
    @FXML
    public void handleCancel(ActionEvent event) {
        Platform.exit();
    }
    
    private void showFeedback(String message, boolean isError) {
        if (feedbackLabel != null) {
            feedbackLabel.setText(message);
            feedbackLabel.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
            feedbackLabel.setVisible(true);
        }
    }
    
    private void navigateToChangePassword(ActionEvent event, String studentNumber) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nieuw_passw.fxml"));
        Parent root = loader.load();
        
        // Get the NewPasswordController instance
        NewPasswordController newPasswordController = loader.getController();
        
        // Set the student number in the new password controller
        if (newPasswordController != null) {
            newPasswordController.setStudentInfo(studentNumber, passwoord.getText());
        }
        
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Change Password");
        stage.show();
    }
    
    private void navigateToDashboard(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard-view.fxml"));
        Parent root = loader.load();
        
        // Get the DashboardController instance
        DashboardController dashboardController = loader.getController();
        
        // Set the student number in the dashboard
        if (dashboardController != null) {
            dashboardController.setStudentNumber(studentnummer.getText());
        }
        
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Dashboard");
        stage.show();
    }
    
    private void navigateToAdminPage(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/hello-view.fxml"));
        Parent root = loader.load();
        
        // Get the Controller instance
        Controller controller = loader.getController();
        
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Admin Dashboard");
        stage.show();
    }
    
    // Static method to get the current API password
    public static String getUserApiPassword() {
        return userStudentNumber;
    }
}