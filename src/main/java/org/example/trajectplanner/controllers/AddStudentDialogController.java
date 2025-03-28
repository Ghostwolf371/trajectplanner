package org.example.trajectplanner.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.trajectplanner.services.StudentService;
import org.example.trajectplanner.utils.DialogUtils;

import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Random;

public class AddStudentDialogController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<String> majorComboBox;
    @FXML private TextField cohortField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private DatePicker birthdatePicker;

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    private void initialize() {
        majorComboBox.getItems().addAll(
            "SE",
            "BI",
            "NE"
        );

        genderComboBox.getItems().addAll(
            "M",
            "F"
        );
    }

    @FXML
    private void handleSave() {
        if (!validateInputs()) {
            return;
        }

        try {
            // Format first name
            String formattedFirstName = firstNameField.getText().trim();
            
            // Generate student number in the correct format: SE/YYMM/XX
            Calendar now = Calendar.getInstance();
            String year = String.format("%02d", now.get(Calendar.YEAR) % 100);
            String month = String.format("%02d", now.get(Calendar.MONTH) + 1);
            String sequence = String.format("%02d", new Random().nextInt(99));
            String studentNumber = String.format("SE/%s%s/%s", year, month, sequence);
            
            // Format student number for API call
            String apiStudentNumber = studentNumber.replace("/", "-");

            ObjectNode requestBody = mapper.createObjectNode()
                .put("student_number", apiStudentNumber)
                .put("first_name", formattedFirstName)
                .put("last_name", lastNameField.getText().trim())
                .put("major", majorComboBox.getValue())
                .put("cohort", Integer.parseInt(cohortField.getText().trim()))
                .put("gender", genderComboBox.getValue())
                .put("birthdate", birthdatePicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .put("password", DEFAULT_PASSWORD); // Using the same default password as in LoginController


            HttpResponse<String> response = StudentService.create(requestBody.toString());

            if (response != null) {

                handleResponse(response);
            } else {
                showError("Error", "Failed to connect to server");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to save student: " + e.getMessage());
        }
    }

    private void handleResponse(HttpResponse<String> response) {
        switch (response.statusCode()) {
            case 201, 200 -> closeDialog();
            case 400 -> {
                try {
                    JsonNode errorResponse = mapper.readTree(response.body());
                    String errorMessage = errorResponse.has("error") 
                        ? errorResponse.get("error").asText() 
                        : "Please check your input values";
                    showError("Validation Error", errorMessage);
                } catch (Exception e) {
                    showError("Validation Error", "Invalid input data");
                }
            }
            case 409 -> showError("Conflict", "This student already exists");
            default -> showError("Error", 
                String.format("Server returned status code: %d%nResponse: %s", 
                    response.statusCode(), response.body()));
        }
    }

    private boolean validateInputs() {
        String firstName = firstNameField.getText().trim();
        
        if (firstName.isEmpty()) {
            showError("Validation Error", "First name is required");
            return false;
        }
        
        // Very basic validation - just ensure it contains at least one letter
        if (!firstName.matches(".*[a-zA-Z].*")) {
            showError("Validation Error", "First name must contain at least one letter");
            return false;
        }

        if (lastNameField.getText() == null || lastNameField.getText().trim().isEmpty()) {
            showError("Validation Error", "Last name is required");
            return false;
        }

        if (majorComboBox.getValue() == null) {
            showError("Validation Error", "Major is required");
            return false;
        }

        if (cohortField.getText() == null || cohortField.getText().trim().isEmpty()) {
            showError("Validation Error", "Cohort is required");
            return false;
        }

        try {
            Integer.parseInt(cohortField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Validation Error", "Cohort must be a valid number");
            return false;
        }

        if (genderComboBox.getValue() == null) {
            showError("Validation Error", "Gender is required");
            return false;
        }

        if (birthdatePicker.getValue() == null) {
            showError("Validation Error", "Birthdate is required");
            return false;
        }

        return true;
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeDialog() {
        Stage stage = (Stage) firstNameField.getScene().getWindow();
        stage.close();
    }

    private String generateStudentNumber() {
        // Generate a more structured student number
        LocalDate now = LocalDate.now();
        return String.format("S%d%02d%06d", 
            now.getYear(),
            now.getMonthValue(),
            System.currentTimeMillis() % 1000000);
    }

    private String generateDefaultPassword() {
        // Generate a more secure default password
        return "Pass" + generateStudentNumber();
    }

    private static final String DEFAULT_PASSWORD = "9x#V@7p!Lz$Q2w%T8m^C3j*B6r&K0d";
}




























