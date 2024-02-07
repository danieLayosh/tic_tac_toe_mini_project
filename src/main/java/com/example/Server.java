package com.example;

import java.util.ArrayList;

import com.example.IProtocol.ICommunicationHandler;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private enum loginMessage {
        NAME, SIZE
    };

    public static final String PLAYER1 = "1", PLAYER2 = "2";

    public static void main(String[] args) {

        ArrayList<GameManager> games = new ArrayList<GameManager>(); // create a list of games
        try {
            ServerSocket server = new ServerSocket();// make a new server
            server.bind(new java.net.InetSocketAddress(SocketCommunication.getHost(), SocketCommunication.getPort()));
            System.out.println("Server started on port - " + server.getLocalPort() + " - waiting for clients...");

            Runtime.getRuntime().addShutdownHook(new Thread() { // add a shutdown hook
                public void run() {
                    try {
                        System.out.println("Shutting down server...");
                        server.close();
                        System.out.println("Server closed.");
                    } catch (Exception e) {
                        System.err.println("Error closing server: " + e.getMessage());
                    }
                }
            });

            while (true) { // infinite loop
                Socket clientSocket = server.accept(); // accept a new client
                ICommunicationHandler communicationHandler = new SocketCommunication(clientSocket);
                System.out.println("Client connected from " + clientSocket.getInetAddress());

                new Thread(() -> {
                    Boolean flag = true;
                    try {
                        String message = communicationHandler.readMessage();
                        String name = message.split(" ")[loginMessage.NAME.ordinal()];
                        String size = message.split(" ")[loginMessage.SIZE.ordinal()];
                        int clientSize = Integer.parseInt(size);
                        Boolean gameFound = false;
                        GameManager currentgame = null;

                        for (GameManager game : games) {
                            int currentgameSize = game.getBoardSize();
                            if (currentgameSize == clientSize && !game.isFull()) {
                                communicationHandler.send(PLAYER2);
                                game.setCommunicationHandler(communicationHandler);
                                game.setPlayer2(name);
                                gameFound = true;
                                currentgame = game;
                                games.remove(game);
                                break;
                            }
                        }

                        if (gameFound == false) {
                            communicationHandler.send(PLAYER1);
                            currentgame = new GameManager(clientSize, name, communicationHandler);
                        }

                        games.add(currentgame);
                        System.out.println("Game board size - " + size);

                        clientLoop(flag, communicationHandler, clientSocket, currentgame, games);

                    } catch (Exception e) {
                        System.out.println("Error: " + e);
                        flag = false;
                        try {
                            clientSocket.close();
                            System.out.println("Client disconnected from " + clientSocket.getInetAddress());
                        } catch (Exception e2) {
                            System.out.println("Error: " + e2);
                        }
                    }
                }).start();
            }

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    private static void clientLoop(Boolean flag, ICommunicationHandler communicationHandler, Socket clientSocket,
            GameManager currentgame, ArrayList<GameManager> games) {
        while (flag && !clientSocket.isClosed()) {
            String message = communicationHandler.readMessage();
            String[] msgArr = message.split(" ");
            String command = msgArr[0].trim().toLowerCase(); // Normalize the command string

            switch (command) {
                case "isfull":
                    communicationHandler.send("isFull " + currentgame.isFull());
                    break;
                case "getboardsize":
                    communicationHandler.send(String.valueOf(currentgame.getBoardSize()));
                    break;
                case "changeboard":
                    currentgame.changeBoard(Integer.parseInt(msgArr[1]), Integer.parseInt(msgArr[2]),
                            Integer.parseInt(msgArr[3]));
                    break;
                case "getboard":
                    communicationHandler.send(String
                            .valueOf(currentgame.getBoard(Integer.parseInt(msgArr[1]), Integer.parseInt(msgArr[2]))));
                    break;
                case "getplayers":
                    String[] players = currentgame.getPlayers();
                    communicationHandler.send("getPlayers " + players[0] + " " + players[1]);
                    break;
                case "close":
                    currentgame.close();
                    games.remove(currentgame);
                    communicationHandler.close();
                    flag = false;
                    System.out.println("Client disconnected from " + clientSocket.getInetAddress());
                    break;
                default:
                    System.out.println("Received unknown command: " + command);
                    break;
            }
        }
    }
}