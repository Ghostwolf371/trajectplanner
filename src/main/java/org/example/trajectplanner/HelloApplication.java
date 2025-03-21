package org.example.trajectplanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.http.HttpResponse;

import org.example.trajectplanner.API.GetMethods;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1600, 900);
        stage.setTitle("Hello sekai lol");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {

        GetMethods getMethods = new GetMethods();

        HttpResponse<String> response = getMethods.getExams();
        System.out.println(response.body());
        launch();
    }
}