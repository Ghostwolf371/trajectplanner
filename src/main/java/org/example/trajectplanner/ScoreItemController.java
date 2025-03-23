package org.example.trajectplanner;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.example.trajectplanner.API.ScoreService;
import org.example.trajectplanner.Modal.Score;
import org.example.trajectplanner.utils.DialogUtils;

public class ScoreItemController {
    @FXML private Label id;
    @FXML private Label studentNumber;
    @FXML private Label courseName;
    @FXML private Label scoreValue;
    @FXML private Label scoreDate;
    @FXML private Button deleteButton;
    @FXML private Button editButton;
    
    private String scoreId;

    @FXML
    private void initialize() {
        editButton.setOnAction(event -> openEditDialog());
        deleteButton.setOnAction(event -> handleDelete());
    }

    public void setData(Score score) {
        if (score == null) {
            throw new IllegalArgumentException("Score cannot be null");
        }
        
        this.scoreId = score.getId();
        id.setText(score.getId());
        studentNumber.setText(score.getStudentNumber());
        courseName.setText(score.getCourseName());
        scoreValue.setText(String.valueOf(score.getScoreValue()));
        scoreDate.setText(score.getScoreDateTime());
    }

    public String getStudentNumber() {
        return studentNumber.getText();
    }

    public String getCourseName() {
        return courseName.getText();
    }

    private void openEditDialog() {
        try {
            var dialog = DialogUtils.showDialog(
                "/org/example/trajectplanner/edit-score.fxml",
                "Edit Score",
                editButton.getScene().getWindow()
            );
            
            var controller = dialog.getUserData();
            if (controller instanceof EditScoreController) {
                ((EditScoreController) controller).setScoreId(scoreId);
            }
        } catch (Exception e) {
            DialogUtils.showError("Error", "Failed to open edit window: " + e.getMessage());
        }
    }

    private void handleDelete() {
        if (scoreId == null || scoreId.trim().isEmpty()) {
            DialogUtils.showError("Delete Failed", "Invalid score ID");
            return;
        }

        try {
            var response = ScoreService.delete(scoreId);
            if (response != null) {
                switch (response.statusCode()) {
                    case 200 -> hideScoreItem();
                    case 404 -> DialogUtils.showError("Delete Failed", "Score not found");
                    case 400 -> DialogUtils.showError("Delete Failed", "Invalid score ID format");
                    default -> DialogUtils.showError("Delete Failed", 
                        String.format("Server error occurred (Status: %d)", 
                        response.statusCode()));
                }
            } else {
                DialogUtils.showError("Delete Failed", "Could not connect to server");
            }
        } catch (Exception e) {
            DialogUtils.showError("Error", "Failed to delete score: " + e.getMessage());
        }
    }

    private void hideScoreItem() {
        id.getParent().setVisible(false);
    }
}
