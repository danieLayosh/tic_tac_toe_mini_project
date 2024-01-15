package com.example;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class GUI implements Initializable, gameInterface {
    private Game game;
    private int value;

    @FXML
    private Label centerLabel;

    @FXML
    private GridPane gridPane;

    @FXML
    private Label player2Name;

    @FXML
    private Label playerName;

    public GUI(int value) {
        this.value = value;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void refresh() {
        updatePlayerNames();
        playersJoined();
    }

    public void createBoard() {
        Platform.runLater(() -> {
            if (!game.isFull()) {
                return;
            }

            int size = this.game.getBoardSize();
            VBox.setVgrow(gridPane, Priority.ALWAYS);
            VBox.setMargin(centerLabel, null);
            gridPane.getChildren().clear();

            // Clear existing column and row constraints
            gridPane.getColumnConstraints().clear();
            gridPane.getRowConstraints().clear();

            for (int i = 0; i < size; i++) {
                ColumnConstraints cc = new ColumnConstraints();
                cc.setHgrow(Priority.ALWAYS); // allow column to grow
                cc.setFillWidth(true); // ask nodes to fill space for column
                gridPane.getColumnConstraints().add(cc);

                RowConstraints rc = new RowConstraints();
                rc.setVgrow(Priority.ALWAYS); // allow row to grow
                rc.setFillHeight(true); // ask nodes to fill height for row
                gridPane.getRowConstraints().add(rc);

                for (int j = 0; j < size; j++) {
                    Button bt = new Button();
                    bt.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                    bt.setOnAction((ActionEvent event) -> {
                        int x = GridPane.getColumnIndex(bt);
                        int y = GridPane.getRowIndex(bt);
                        game.changeBoard(x, y, value);
                    });

                    bt.getStyleClass().add("button");
                    bt.setStyle("-fx-font-size: 35;");
                    gridPane.add(bt, i, j);
                }
            }
        });
    }

    public void disableBoard() {
        int size = game.getBoardSize();
        for (int i = 0; i < size; i++) {
            gridPane.addRow(i);
            for (int j = 0; j < size; j++) {
                gridPane.addColumn(j);
                Button bt = (Button) gridPane.getChildren().get(i * size + j);
                bt.setDisable(true);
            }
        }
    }

    public void enableBoard() {
        int size = game.getBoardSize();
        for (int i = 0; i < size; i++) {
            gridPane.addRow(i);
            for (int j = 0; j < size; j++) {
                gridPane.addColumn(j);
                Button bt = (Button) gridPane.getChildren().get(i * size + j);
                if (game.getBoard()[i][j] != 0) {
                    bt.setDisable(true);
                    continue;
                }
                bt.setDisable(false);
            }
        }
    }

    private void playersJoined() {
        if (game.isFull()) {
            centerLabel.setStyle("-fx-font-size: 0;"); // Set font size
            centerLabel.setText("");
            centerLabel.disableProperty();
        } else {
            centerLabel.setStyle("-fx-font-size: 40;"); // Set font size
            StackPane.setAlignment(centerLabel, javafx.geometry.Pos.CENTER);
            centerLabel.setText("Waiting for players...");
        }
    }

    private void updatePlayerNames() {
        playerName.setText("player 1 is: " + game.getPlayers()[0]);
        if (game.isFull()) {
            player2Name.setText("player 2 is: " + game.getPlayers()[1]);
        } else {
            player2Name.setText("waiting for player 2");
        }
    }

    public void win() {
        centerLabel.setStyle("-fx-font-size: 40;"); // Set font size
        StackPane.setAlignment(centerLabel, javafx.geometry.Pos.CENTER);
        centerLabel.setText("You win!");
        gridPane.visibleProperty().set(false);
    }

    public void lose() {
        centerLabel.setStyle("-fx-font-size: 40;"); // Set font size
        StackPane.setAlignment(centerLabel, javafx.geometry.Pos.CENTER);
        centerLabel.setText("You lose!");
        gridPane.visibleProperty().set(false);
    }

    public void draw() {
        centerLabel.setStyle("-fx-font-size: 40;"); // Set font size
        StackPane.setAlignment(centerLabel, javafx.geometry.Pos.CENTER);
        centerLabel.setText("Draw");
        gridPane.visibleProperty().set(false);
    }

    public void updateBoard() {
        int[][] grid = game.getBoard();
        int size = game.getBoardSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Button bt = (Button) gridPane.getChildren().get(i * size + j);
                bt.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                if (grid[i][j] == 1) {
                    bt.setText("X");
                    bt.setDisable(true);
                } else if (grid[i][j] == 2) {
                    bt.setText("O");
                    bt.setDisable(true);
                }
            }
        }
        // for (int i = 0; i < grid.length; i++) {
        //     System.out.print("[ ");
        //     for (int g = 0; g < grid.length; g++) {
        //         System.out.print(grid[i][g] + ",");
        //     }
        //     System.out.println("]");
        // }
    }

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        refresh();
    }

}
