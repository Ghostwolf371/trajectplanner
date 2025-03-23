package org.example.trajectplanner;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class EditTentamenController {
    @FXML
    private Label tentamenIdLabel;
    
    private String tentamenId;
    
    public void setTentamenId(String id) {
        this.tentamenId = id;
        tentamenIdLabel.setText("Tentamen ID: " + id);
        // Load tentamen data using the ID
    }
}