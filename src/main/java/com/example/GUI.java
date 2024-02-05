package com.example;

// import java.net.Socket;

import com.example.IProtocol.ICommunicationHandler;

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
    // private Socket gameSocket;
    private ICommunicationHandler gameSocketProtocol;
    private int value;
    private int boardSize;
    private String isFull;
    private int[][] board;
    // private String player1;
    // private String player2;

    @FXML
    private Label centerLabel;

    @FXML
    private GridPane gridPane;

    @FXML
    private Label player2Name;

    @FXML
    private Label player1Name;

    public GUI(int value, int boardSize) {
        this.value = value;
        this.boardSize = boardSize;
        this.board = new int[boardSize][boardSize];
    }

    void setIsFull(String isFull) {
        this.isFull = isFull;
    }

    // void setPlayer1(String player1) {
    //     this.player1 = player1;
    // }

    // void setPlayer2(String player2) {
    //     this.player2 = player2;
    // }

    public void setGame(ICommunicationHandler gameSocketProtocol) {
        this.gameSocketProtocol = gameSocketProtocol;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public void updateBoard(int x, int y, int value) {
        this.board[x][y] = value;
        refreshBoard();
    }

    public void switch_turn_lable(Boolean isPlayerTurn) {
        if (isPlayerTurn) {
            player1Name.setText("your turn");
        } else {
            player1Name.setText("opponent's turn");
        }
    }

    // protocol related method
    public void refresh() {
        gameSocketProtocol.send("getPlayers");
        gameSocketProtocol.send("isFull");
    }

    public void createBoard() {
        try {
            if (isFull == "false") {
                return;
            }
            VBox.setVgrow(gridPane, Priority.ALWAYS);
            VBox.setMargin(centerLabel, null);
            gridPane.getChildren().clear();

            // Clear existing column and row constraints
            gridPane.getColumnConstraints().clear();
            gridPane.getRowConstraints().clear();
            for (int i = 0; i < boardSize; i++) {
                ColumnConstraints cc = new ColumnConstraints();
                cc.setHgrow(Priority.ALWAYS); // allow column to grow
                cc.setFillWidth(true); // ask nodes to fill space for column
                gridPane.getColumnConstraints().add(cc);
                RowConstraints rc = new RowConstraints();
                rc.setVgrow(Priority.ALWAYS); // allow row to grow
                rc.setFillHeight(true); // ask nodes to fill height for row
                gridPane.getRowConstraints().add(rc);
                for (int j = 0; j < boardSize; j++) {
                    Button bt = new Button();
                    bt.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                    bt.setOnAction((ActionEvent event) -> {
                        int x = GridPane.getColumnIndex(bt);
                        int y = GridPane.getRowIndex(bt);
                        try {
                            gameSocketProtocol.send(String.format("changeBoard %d %d %d", x, y, value));
                        } catch (Exception e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                    });

                    bt.getStyleClass().add("button");
                    bt.setStyle("-fx-font-size: 35;");
                    if (value == 2) {
                        bt.setDisable(true);
                    }
                    gridPane.add(bt, i, j);

                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void disableBoard() {
        try {
            for (int i = 0; i < boardSize; i++) {
                gridPane.addRow(i);
                for (int j = 0; j < boardSize; j++) {
                    gridPane.addColumn(j);
                    Button bt = (Button) gridPane.getChildren().get(i * boardSize + j);
                    bt.setDisable(true);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void enableBoard() {
        try {
            for (int i = 0; i < boardSize; i++) {
                gridPane.addRow(i);
                for (int j = 0; j < boardSize; j++) {
                    gridPane.addColumn(j);
                    Button bt = (Button) gridPane.getChildren().get(i * boardSize + j);
                    if (board[i][j] != 0) {
                        bt.setDisable(true);
                        continue;
                    }
                    bt.setDisable(false);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void playersJoined() {
        try {
            if (isFull.equals("true")) {
                centerLabel.setStyle("-fx-font-size: 0;"); // Set font size
                centerLabel.setText("");
                centerLabel.disableProperty();
            } else {
                centerLabel.setStyle("-fx-font-size: 40;"); // Set font size
                StackPane.setAlignment(centerLabel, javafx.geometry.Pos.CENTER);
                centerLabel.setText("Waiting for players...");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void setOpponentsLable(String name){
        System.out.println("opponent's name: " + name);
        player2Name.setText("opponent's name: " + name);
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

    // refresh the board
    public void refreshBoard() {
        try {
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    Button bt = (Button) gridPane.getChildren().get(i * boardSize + j);
                    bt.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                    if (board[i][j] == 1) {
                        bt.setText("X");
                        bt.setDisable(true);
                    } else if (board[i][j] == 2) {
                        bt.setText("O");
                        bt.setDisable(true);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        refresh();
    }

}
