package org.example.trajectplanner.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.trajectplanner.services.ExamService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Arrays;

public class EditTentamenController {
    @FXML
    private Label tentamenIdLabel;
    
    @FXML
    private ComboBox<String> courseCodeComboBox; // Changed from TextField to ComboBox
    
    @FXML
    private ComboBox<String> typeComboBox;
    
    @FXML
    private DatePicker datePicker;
    
    private String tentamenId;
    
    @FXML
    public void initialize() {
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
    }
    
    public void setTentamenId(String id) {
        this.tentamenId = id;
        tentamenIdLabel.setText("Editing examination " + id);
        loadExamData();
    }
    
    private void loadExamData() {
        try {
            HttpResponse<String> response = ExamService.getById(tentamenId);
            
            if (response == null) {
                showError("Error", "Failed to connect to server");
                return;
            }
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response.body());
            
            // Check if response is an error
            if (jsonNode.has("status") && jsonNode.get("status").asText().equals("error")) {
                String errorMessage = jsonNode.has("message") ? 
                    jsonNode.get("message").asText() : 
                    "Unknown error occurred";
                showError("Database Error", errorMessage);
                return;
            }
            
            if (response.statusCode() == 200) {
                // Handle single exam object
                if (jsonNode.has("code")) {
                    String courseCode = jsonNode.get("code").asText();
                    courseCodeComboBox.setValue(courseCode);
                }
                
                if (jsonNode.has("exam_type")) {
                    String examType = jsonNode.get("exam_type").asText();
                    typeComboBox.setValue(examType);
                }
                
                if (jsonNode.has("exam_date")) {
                    String dateStr = jsonNode.get("exam_date").asText();
                    if (dateStr != null && !dateStr.isEmpty()) {
                        try {
                            LocalDate date = LocalDate.parse(dateStr);
                            datePicker.setValue(date);
                        } catch (Exception e) {
                            System.out.println("Date parse error: " + e.getMessage());
                            showError("Error", "Invalid date format in data");
                        }
                    }
                }
            } else {
                showError("Error", "Failed to load examination data. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to load examination data: " + e.getMessage());
        }
    }
    

    @FXML
    private void handleUpdate() {
        if (!validateInputs()) {
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode requestBody = mapper.createObjectNode();
            
            try {
                int examIdInt = Integer.parseInt(tentamenId);
                requestBody.put("exam_id", examIdInt);
            } catch (NumberFormatException e) {
                showError("Invalid Input", "Invalid examination ID format");
                return;
            }

            String courseCode = courseCodeComboBox.getValue();
            if (courseCode != null && !courseCode.isEmpty()) {
                requestBody.put("code", courseCode);
            }

            String selectedType = typeComboBox.getValue();
            if (selectedType != null && !selectedType.isEmpty()) {
                requestBody.put("exam_type", selectedType);
            }
            
            LocalDate selectedDate = datePicker.getValue();
            if (selectedDate != null) {
                String formattedDate = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
                requestBody.put("exam_date", formattedDate);
            }

            HttpResponse<String> response = ExamService.update(requestBody.toString());
            handleUpdateResponse(response);
            
        } catch (Exception e) {
            showError("Error", "Failed to update examination: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleUpdateResponse(HttpResponse<String> response) {
        if (response != null) {
            String responseBody = response.body();
            switch (response.statusCode()) {
                case 200:
                    closeDialog();
                    break;
                case 400:
                    String errorMessage = "Please check your input values.";
                    if (responseBody != null && !responseBody.isEmpty()) {
                        errorMessage += "\nServer message: " + responseBody;
                    }
                    showError("Invalid Input", errorMessage);
                    break;
                case 404:
                    showError("Not Found", "The examination could not be found.");
                    break;
                case 500:
                    String dbErrorMessage = "A database error occurred.";
                    if (responseBody != null && !responseBody.isEmpty()) {
                        dbErrorMessage += "\nServer message: " + responseBody;
                    }
                    showError("Server Error", dbErrorMessage);
                    break;
                default:
                    showError("Update Failed", "Unexpected status code: " + response.statusCode() + 
                        "\nResponse: " + responseBody);
            }
        } else {
            showError("Connection Error", "Failed to connect to the server");
        }
    }

    private void closeDialog() {
        Stage stage = (Stage) courseCodeComboBox.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void handleCancel() {
        closeDialog();
    }
    
    private boolean validateInputs() {
        if (courseCodeComboBox.getValue() == null || courseCodeComboBox.getValue().trim().isEmpty()) {
            showError("Validation Error", "Course code is required");
            return false;
        }
        
        if (typeComboBox.getValue() == null) {
            showError("Validation Error", "Exam type must be selected");
            return false;
        }
        
        if (datePicker.getValue() == null) {
            showError("Validation Error", "Date must be selected");
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
}
