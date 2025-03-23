package org.example.trajectplanner;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.trajectplanner.API.PostMethods;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.net.http.HttpResponse;

public class AddExamDialogController {
    @FXML
    private TextField cursusNameField;
    @FXML
    private TextField datumField;
    @FXML
    private TextField typeField;

    @FXML
    private void handleAdd() {
        try {
            String cursusName = cursusNameField.getText().trim();
            String datum = datumField.getText().trim();
            String type = typeField.getText().trim();

            if (cursusName.isEmpty()) {
                showAlert("Validation Error", "Course name is required.");
                return;
            }

            if (!type.equals("Regulier") && !type.equals("Her")) {
                showAlert("Validation Error", "Exam type must be exactly 'Regulier' or 'Her'");
                return;
            }

            if (!datum.isEmpty() && !datum.matches("\\d{4}-\\d{2}-\\d{2}")) {
                showAlert("Validation Error", "Date must be in format YYYY-MM-DD");
                return;
            }

            HashMap<String, String> requestMap = new HashMap<>();
            requestMap.put("code", cursusName);
            requestMap.put("exam_type", type);
            if (!datum.isEmpty()) {
                requestMap.put("exam_date", datum);
            }

            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(requestMap);
            
            PostMethods postMethods = new PostMethods();
            HttpResponse<String> response = postMethods.postExam(requestBody);

            if (response != null) {
                if (response.statusCode() == 201) {
                    showAlert("Success", "Exam added successfully!");
                    closeDialog();
                } else {
                    ObjectMapper errorMapper = new ObjectMapper();
                    String errorMessage = errorMapper.readTree(response.body()).path("error").asText();
                    showAlert("Error", "Failed to add exam: " + errorMessage);
                }
            } else {
                showAlert("Error", "Failed to add exam. No response received from server.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) cursusNameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
