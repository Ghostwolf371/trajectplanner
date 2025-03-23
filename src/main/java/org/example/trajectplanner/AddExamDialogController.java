package org.example.trajectplanner;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.example.trajectplanner.API.PostMethods;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.scene.control.Alert;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Arrays;

public class AddExamDialogController {
    @FXML
    private ComboBox<String> courseCodeComboBox;
    
    @FXML
    private DatePicker datePicker;
    
    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    public void initialize() {
        // Initialize exam type combo box
        typeComboBox.getItems().addAll("Regulier", "Her");
        
        // Initialize course code combo box with all course codes
        List<String> courseCodes = Arrays.asList(
            "SP1121BB1", "SP1121BB2", "SP1121BB3a", "SP1121BB3b", "SP1121BB4",
            "SP1121BB5a", "SP1121BB5b", "SP1121BB6a", "SP1121BB6b", "SP1121BB7",
            "21-ID1-T", "21-ID2-T", "SP1121ID3", "SP1121ID4", "SP1121ID5",
            "SP1121ID6", "SH11121MS1", "SH11121MS2", "SH11121MS3", "SH11121MS4",
            "SH11121MS5", "SH11121MS6", "SH1120SE1", "SH1120SE2", "SH1120SE3",
            "SH1120SE4", "SH1120SE5", "SH1120SE6", "SH21119CS1", "SH21119CS2",
            "SH21119CS3", "SH21119CS4", "SH21119CS5", "SH21119CS6", "SH21119PM1",
            "SH21119PM2", "SH21119PM3", "SH21119PM4", "SH21119PM5", "SH21119PM6",
            "7-PP1-T", "7-PP2-T", "7-PP3A-T", "7-PP3B-T", "7-PP4-T",
            "7-PP5A-T", "7-PP5B-T", "7-PP6-T", "7-PP7A-T", "7-PP7B-T",
            "8-AO1-T", "8-AO2-T", "8-AO3-T"
        );
        courseCodeComboBox.getItems().addAll(courseCodes);
        
        // Set default values
        datePicker.setPromptText("YYYY-MM-DD");
    }

    @FXML
    private void handleSave() {
        if (!validateInputs()) {
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode requestBody = mapper.createObjectNode();
            
            // Get selected course code instead of text
            String selectedCourseCode = courseCodeComboBox.getValue();
            if (selectedCourseCode != null) {
                requestBody.put("code", selectedCourseCode);
            }
            
            requestBody.put("exam_type", typeComboBox.getValue());
            
            if (datePicker.getValue() != null) {
                String formattedDate = datePicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE);
                requestBody.put("exam_date", formattedDate);
            }

            PostMethods postMethods = new PostMethods();
            HttpResponse<String> response = postMethods.postExam(requestBody.toString());

            if (response != null) {
                if (response.statusCode() == 200) {
                    closeDialog();
                } else {
                    showError("Failed to add exam", "Server returned status code: " + response.statusCode());
                }
            } else {
                showError("Error", "Failed to connect to server");
            }
        } catch (Exception e) {
            showError("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (courseCodeComboBox.getValue() == null || courseCodeComboBox.getValue().trim().isEmpty()) {
            showError("Validation Error", "Please select a course code");
            return false;
        }
        
        if (typeComboBox.getValue() == null) {
            showError("Validation Error", "Exam type is required");
            return false;
        }

        return true;
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) courseCodeComboBox.getScene().getWindow();
        stage.close();
    }
}
