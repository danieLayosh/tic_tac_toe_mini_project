package com.example;

import java.util.ArrayList;

import com.example.IProtocol.ICommunicationHandler;
import com.example.dataBase.Model.GameModel;
import com.example.dataBase.Model.PlayerModel;
import com.example.dataBase.Model.Collection.GameList;
import com.example.dataBase.Model.Collection.PlayerList;
import com.example.dataBase.ViewModel.GameDB;
import com.example.dataBase.ViewModel.PlayerDB;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;

public class Server {
    private enum loginMessage {
        NAME, SIZE
    };

    public static final String PLAYER1 = "1", PLAYER2 = "2";

    public static void main(String[] args) {
        // System.out.println("print the current date:" + new java.util.Date());
        // System.out.println("print the current time:" + new
        // java.util.Date().getTime());

        // PlayerDB playerDB = new PlayerDB();
        // PlayerModel player = new PlayerModel("tammy2");
        // playerDB.insert(player);
        // playerDB.saveChanges();

        // Initialize database access objects
        PlayerDB playerDB = new PlayerDB();
        GameDB gameDB = new GameDB();

        // Example player names
        String player1Name = "test1";
        String player2Name = "test2";

        // Insert players
        PlayerModel player1 = new PlayerModel();
        player1.setPlayerName(player1Name);
        playerDB.insert(player1);

        PlayerModel player2 = new PlayerModel();
        player2.setPlayerName(player2Name);
        playerDB.insert(player2);

        // Commit changes to insert players
        playerDB.saveChanges();

        // Insert a game
        GameModel game2 = new GameModel();
        game2.setPlayer1(player1);
        game2.setPlayer2(player2);
        game2.setWinner(player1); // Let's say player1 wins
        game2.setBoardSize(3); // Example board size
        game2.setResult(GameModel.Result.WIN); // Example result
        game2.setStartTime(new Timestamp(System.currentTimeMillis()));
        game2.setEndTime(new Timestamp(System.currentTimeMillis() + 50000));
        gameDB.insert(game2);

        // Commit changes to insert the game2
        gameDB.saveChanges();

        // Select and print all players
        System.out.println("All Players:");
        PlayerList allPlayers = playerDB.selectAll();
        allPlayers.forEach(p -> System.out.println(p.getPlayerName()));

        // Select and print all games
        System.out.println("\nAll Games:");
        GameList allGames = gameDB.selectAll();
        allGames.forEach(g -> System.out.println("Game ID: " + g.getId() + ", Winner: "
                + ((g.getWinner() != null) ? g.getWinner().getPlayerName() : "NO WINNER")));

        // Update a player's name (not implemented in provided code snippets)
        // playerDB.update(player1, "AliceUpdated");
        // playerDB.saveChanges();

        // Delete a player (not implemented in provided code snippets)
        // playerDB.delete(player1);
        // playerDB.saveChanges();

        // Note: The saveChanges method must be adapted to execute the SQL operations
        // accumulated in the insert, update, and delete methods.

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