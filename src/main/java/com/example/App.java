package com.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("lobby"), 640, 480);
        stage.setTitle("Tic Tac Toe");
        stage.setScene(scene);
        setPrimaryStage(stage);
        // Set the default close operation
        stage.setOnCloseRequest(event -> {
            // Close all other windows
            Platform.exit();
        });

        stage.show();
    }

    static void setRoot(FXMLLoader loader) throws IOException {
        scene.setRoot(loader.load());
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    private void setPrimaryStage(Stage stage) {
        App.primaryStage = stage;
    }

    static public Stage getPrimaryStage() {
        return App.primaryStage;
    }
}