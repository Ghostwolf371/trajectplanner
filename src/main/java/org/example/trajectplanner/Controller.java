package org.example.trajectplanner;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.example.trajectplanner.API.GetMethods;
import org.example.trajectplanner.Modal.Tentamen;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;

public class Controller implements Initializable {
    @FXML
    private VBox tentamenLayout;
    @FXML
    private Button addButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            GetMethods getMethods = new GetMethods();

            HttpResponse<String> response = getMethods.getExams();

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                List<Tentamen> tentamens = mapper.readValue(
                    response.body(), 
                    mapper.getTypeFactory().constructCollectionType(List.class, Tentamen.class)
                );

                for (Tentamen tentamen : tentamens) {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("Tentamen_item.fxml"));

                    try {
                        HBox hBox = fxmlLoader.load();
                        TentamenItemController tic = fxmlLoader.getController();
                        tic.setData(tentamen);
                        tentamenLayout.getChildren().add(hBox);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        addButton.setOnAction(event -> navigateToAdd());
    }

    private void navigateToAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddExamDialog.fxml"));
            Parent root = loader.load();
            
            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); // Make it modal (blocks interaction with other windows)
            popupStage.initOwner(addButton.getScene().getWindow()); // Set the parent window
            
            // Set up the new scene
            Scene scene = new Scene(root);
            popupStage.setScene(scene);
            popupStage.setTitle("Add Tentamen");
            
            // Show the popup
            popupStage.showAndWait(); // This will block until the popup is closed
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
