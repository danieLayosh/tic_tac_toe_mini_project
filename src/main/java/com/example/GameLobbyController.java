package com.example;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


public class GameLobbyController {
    @FXML
    private Button btEnterGame;

    @FXML
    private TextField txSizeInput;

    @FXML
    private TextField txNameInput;

    @FXML
    public void Init() throws IOException {
        new Thread(() -> {
            try {
                int boardSize = Integer.parseInt(this.txSizeInput.getText());
                Platform.runLater(() -> {
                    try {
                        initializeGameSession(boardSize);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (NumberFormatException e) {
                Platform.runLater(() -> {
                    System.out.println("Error: Grid size must be an integer");
                });
            }
        }).start();
    }

    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    private void initializeGameSession(int boardSize) throws IOException {
        try {
            if (boardSize < 3) {
                System.out.println("Error: Grid size must be at least 3");
            } else if (boardSize > 10) {
                System.out.println("Error: Grid size must be at most 10");
            } else {
                GUI gui = new GUI(1, boardSize);
                Socket gameSocket = new Socket(ProtocolS.getHost(), ProtocolS.getProt());
                ProtocolS socket = new ProtocolS(gameSocket);
                gui.setGame(gameSocket, socket);
                socket.send(txNameInput.getText() + " " + boardSize);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("gui.fxml"));
                loader.setController(gui);
                App.getPrimaryStage().setScene(new Scene(loader.load(), 640, 480));
                App.getPrimaryStage().setOnCloseRequest(e -> {
                    socket.send("close");
                    isRunning.set(false);
                });
                new Thread(() -> {
                    try {
                        while (isRunning.get()) {
                            String msg = socket.res();
                            String[] msgArr = msg.split(" ");
                            Platform.runLater(() -> {
                                if (msgArr[0].equals("1") || msgArr[0].equals("2")) {
                                    gui.setValue(Integer.parseInt(msgArr[0]));
                                } else if (msgArr[0].equals("refreshBoard")) {
                                    gui.updateBoard(Integer.parseInt(msgArr[1]), Integer.parseInt(msgArr[2]),
                                            Integer.parseInt(msgArr[3]));
                                } else if (msgArr[0].equals("enableBoard")) {
                                    gui.enableBoard();
                                } else if (msgArr[0].equals("disableBoard")) {
                                    gui.disableBoard();
                                } else if (msgArr[0].equals("win")) {
                                    gui.win();
                                } else if (msgArr[0].equals("lose")) {
                                    gui.lose();
                                } else if (msgArr[0].equals("draw")) {
                                    gui.draw();
                                } else if (msgArr[0].equals("refresh")) {
                                    gui.refresh();
                                } else if (msgArr[0].equals("createBoard")) {
                                    gui.createBoard();
                                } else if (msgArr[0].equals("getPlayers")) {
                                    gui.setPlayer1(msgArr[1]);
                                    gui.setPlayer2(msgArr[2]);
                                    gui.updatePlayersNames();
                                } else if (msgArr[0].equals("isFull")) {
                                    gui.setIsFull(msgArr[1]);
                                    gui.playersJoined();
                                } else if (msgArr[0].equals("close")) {
                                    socket.close();
                                    isRunning.set(false);
                                }
                            });
                        }
                    } catch (Exception e) {
                        System.out.println("Error: " + e);
                        isRunning.set(false);
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void enterGame(ActionEvent event) throws IOException {
        Init();
    }

}
