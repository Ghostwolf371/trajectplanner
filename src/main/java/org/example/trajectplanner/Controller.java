package org.example.trajectplanner;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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

public class Controller implements Initializable {
    @FXML
    private VBox tentamenLayout;
    @FXML
    private Button addButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://trajectplannerapi.dulamari.com/exams/"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add-tentamen.fxml"));
            Parent root = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) addButton.getScene().getWindow();
            // Replace the current scene
            stage.setScene(new Scene(root));
            stage.setTitle("Add Tentamen");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
