package com.example;

import java.io.IOException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;

public class Host {
    private ArrayList<Game> games = new ArrayList<Game>();

    @FXML
    private Button btEnterGame;

    @FXML
    private TextField txSizeInput;

    @FXML
    private TextField txNameInput;

    @FXML
    public void Init() throws IOException {
        try {
            int size = Integer.parseInt(this.txSizeInput.getText());
            if (size < 3) {
                System.out.println("Error: Grid size must be at least 3");
            } else if (size > 200) {
                System.out.println("Error: Grid size must be at most 7");
            } else {
                for (Game game : games) {
                    if (game.getBoardSize() == size && !game.isFull()) {
                        this.joinGame(game);
                        games.remove(game);
                        return;
                    }
                }
                GUI gui = new GUI(1);
                Game game = new Game(size, txNameInput.getText(), gui);
                games.add(game);
                gui.setGame(game);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("gui.fxml"));
                loader.setController(gui);
                // create new window
                Scene scene = new Scene(loader.load(), 640, 480);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("You are - X");
                stage.show();
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Grid size must be an integer");
        }
    }

    @FXML
    public void joinGame(Game game) throws IOException {
        GUI gui = new GUI(2);
        gui.setGame(game);
        game.setGUI(gui);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui.fxml"));
        loader.setController(gui);
        Scene scene = new Scene(loader.load(), 640, 480);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("You are - O");
        stage.show();
        game.setPlayer2(txNameInput.getText());
        game.switchBoard(2);
    }

    @FXML
    void enterGame(ActionEvent event) throws IOException {
        Init();
    }

    @FXML
    private void initialize() {
        txSizeInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                System.out.println("Enter Pressed");
                try {
                    Init();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
