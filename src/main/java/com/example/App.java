package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("lobby"), 640, 480);
        stage.setTitle("Tic Tac Toe - GAME LOBBY");
        stage.setScene(scene);
        setPrimaryStage(stage);
        stage.show();
    }

    static void setRoot(FXMLLoader loader) throws IOException {
        scene.setRoot(loader.load());
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    private void setPrimaryStage(Stage stage) {
        App.primaryStage = stage;
    }

    static public Stage getPrimaryStage() {
        return App.primaryStage;
    }
}