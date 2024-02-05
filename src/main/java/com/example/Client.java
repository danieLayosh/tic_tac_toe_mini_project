package com.example;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import com.example.IProtocol.ICommunicationHandler;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class Client {
    @FXML
    private Button btEnterGame;

    @FXML
    private TextField txSizeInput;

    @FXML
    private TextField txNameInput;

    private final AtomicBoolean isRunning = new AtomicBoolean(true);

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
                    System.out.println("Error: board size must be an integer");
                });
            }
        }).start();
    }

    private void initializeGameSession(int boardSize) throws IOException {
        if (boardSize < 3 || boardSize > 10) {
            System.out.println("Error: Board size must be between 3 and 10");
            return;
        }
        try {
            GUI gui = new GUI(1, boardSize);
            Socket gameSocket = new Socket(SocketCommunication.getHost(), SocketCommunication.getPort());
            ICommunicationHandler socket = new SocketCommunication(gameSocket);
            gui.setGame(socket); // set the game socket
            socket.send(txNameInput.getText() + " " + boardSize); // send the name and board size
            FXMLLoader loader = new FXMLLoader(getClass().getResource("gui.fxml"));
            loader.setController(gui);
            Platform.runLater(() -> {
                try {
                    App.getPrimaryStage().setScene(new Scene(loader.load(), 640, 480));
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }
                App.getPrimaryStage().setTitle("Tic Tac Toe - " + txNameInput.getText());
            });

            App.getPrimaryStage().setOnCloseRequest(e -> {
                socket.send("close"); // close the socket
                isRunning.set(false); // close the thread
            });

            // Listen for messages from the server
            new Thread(() -> listenForServerMessages(socket, gui)).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listenForServerMessages(ICommunicationHandler communicationHandler, GUI gui) {
        try {
            while (isRunning.get()) {
                String message = communicationHandler.readMessage();
                if (message == null || message.isEmpty()) {
                    continue;
                }
                String[] msgArr = message.split(" ");
                Platform.runLater(() -> gui_UI_update(gui, communicationHandler, msgArr));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            Platform.runLater(() -> {
                // Optional: Update the GUI or show an error message indicating the
                // disconnection or error state.
            });
            isRunning.set(false);
        } finally {
            try {
                communicationHandler.close();
            } catch (Exception e) {
                System.out.println("Error closing communication handler: " + e.getMessage());
            }
        }
    }

    private void gui_UI_update(GUI gui, ICommunicationHandler socket, String[] msgArr) {
        switch (msgArr[0]) {
            case "1":
            case "2":
                gui.setValue(Integer.parseInt(msgArr[0]));
                break;
            case "refreshBoard":
                gui.updateBoard(
                        Integer.parseInt(msgArr[1]),
                        Integer.parseInt(msgArr[2]),
                        Integer.parseInt(msgArr[3]));
                break;
            case "enableBoard":
                gui.enableBoard();
                break;
            case "disableBoard":
                gui.disableBoard();
                break;
            case "your_turn":
                gui.switch_turn_lable(true);
                break;
            case "not_your_turn":
                gui.switch_turn_lable(false);
                break;
            case "opponentname":
                gui.setOpponentsLable(msgArr[1]);
                break;
            case "win":
                gui.win();
                break;
            case "lose":
                gui.lose();
                break;
            case "draw":
                gui.draw();
                break;
            case "refresh":
                gui.refresh();
                break;
            case "createBoard":
                gui.createBoard();
                break;
            case "getPlayers":
                // gui.setPlayer1(msgArr[1]);
                // gui.setPlayer2(msgArr[2]);
                break;
            case "isFull":
                gui.setIsFull(msgArr[1]);
                gui.playersJoined();
                break;
            case "close":
                try {
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isRunning.set(false);
                break;
            default:
                System.err.println("Unknown command: " + msgArr[0]);
                break;
        }
    }
}
