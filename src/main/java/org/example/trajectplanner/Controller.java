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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class Controller implements Initializable {
    @FXML
    private VBox tentamenLayout;
    @FXML
    private Button addButton;
    @FXML
    private TextField searchField;
    @FXML
    private Button scoresButton;

    private ObservableList<GridPane> examItems = FXCollections.observableArrayList();
    private FilteredList<GridPane> filteredExams;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the filtered list
        filteredExams = new FilteredList<>(examItems, p -> true);
        
        // Add search listener
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredExams.setPredicate(examItem -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                TentamenItemController controller = (TentamenItemController) examItem.getUserData();
                return controller.getCursusNaam().toLowerCase().contains(lowerCaseFilter);
            });
            
            updateExamList();
        });

        loadExams();
        addButton.setOnAction(event -> navigateToAdd());
        scoresButton.setOnAction(event -> navigateToScores());
    }

    private void loadExams() {
        try {
            GetMethods getMethods = new GetMethods();
            HttpResponse<String> response = getMethods.getExams();

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                List<Tentamen> tentamens = mapper.readValue(
                    response.body(), 
                    mapper.getTypeFactory().constructCollectionType(List.class, Tentamen.class)
                );

                examItems.clear();
                for (Tentamen tentamen : tentamens) {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("Tentamen_item.fxml"));

                    try {
                        GridPane gridPane = fxmlLoader.load();
                        TentamenItemController tic = fxmlLoader.getController();
                        tic.setData(tentamen);
                        gridPane.setUserData(tic);
                        examItems.add(gridPane);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                updateExamList();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateExamList() {
        tentamenLayout.getChildren().clear();
        tentamenLayout.getChildren().addAll(filteredExams);
    }

    private void navigateToAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/trajectplanner/AddExamDialog.fxml"));
            Parent root = loader.load();
            
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(addButton.getScene().getWindow());
            
            Scene scene = new Scene(root);
            // Update CSS path to match the actual location
            scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
            popupStage.setScene(scene);
            popupStage.setTitle("Add Examination");
            popupStage.setMinWidth(800);
            popupStage.setMinHeight(600);
            
            popupStage.showAndWait();
            
            // Refresh the exam list after adding
            loadExams();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateToScores() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("scores-view.fxml"));
            Parent root = loader.load();

            Scene scene = scoresButton.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
