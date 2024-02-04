package com.example;

import java.util.ArrayList;
import java.net.ServerSocket;
import java.net.Socket;

public class Host {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket();// make a new server
            server.bind(new java.net.InetSocketAddress(ProtocolS.getHost(), ProtocolS.getProt())); // bind the server to
            System.out.println("Server started on port - " + ProtocolS.getProt() + " - waiting for clients...");

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

            ArrayList<Game> games = new ArrayList<Game>(); // create a list of games
            while (true) { // infinite loop
                Socket client = server.accept(); // accept a new client
                ProtocolS socket = new ProtocolS(client); // create a new protocol
                System.out.println("Client connected from " + client.getInetAddress());
                new Thread(() -> {
                    Boolean flag = true;
                    try {
                        String message = socket.readMessage();
                        String name = message.split(" ")[0];
                        String size = message.split(" ")[1];
                        Game currentgame = null;
                        for (Game game : games) {
                            if (game.getBoardSize() == Integer.parseInt(size) && !game.isFull()) {
                                socket.send("2");
                                game.setSocket(client);
                                game.setPlayer2(name);
                                currentgame = game;
                                games.remove(game);
                                break;
                            }
                        }
                        if (currentgame == null) {
                            socket.send("1");
                            currentgame = new Game(Integer.parseInt(size), name, client);
                        }
                        games.add(currentgame);
                        System.out.println("Game size - " + size);
                        while (flag && !client.isClosed()) {
                            message = socket.readMessage();
                            String[] msgArr = message.split(" ");
                            if (msgArr[0].equals("isFull")) {
                                socket.send("isFull " + String.valueOf(currentgame.isFull()));
                            } else if (msgArr[0].equals("getBoardSize")) {
                                socket.send(String.valueOf(currentgame.getBoardSize()));
                            } else if (msgArr[0].equals("changeBoard")) {
                                currentgame.changeBoard(Integer.parseInt(msgArr[1]), Integer.parseInt(msgArr[2]),
                                        Integer.parseInt(msgArr[3]));
                            } else if (msgArr[0].equals("getBoard")) {
                                socket.send(String.valueOf(
                                        currentgame.getBoard(Integer.parseInt(msgArr[1]),
                                                Integer.parseInt(msgArr[2]))));
                            } else if (msgArr[0].equals("getPlayers")) {
                                String[] players = currentgame.getPlayers();
                                socket.send("getPlayers " + players[0] + " " + players[1]);
                            } else if (msgArr[0].equals("close")) {
                                currentgame.close();
                                games.remove(currentgame);
                                socket.close();
                                flag = false;
                                System.out.println("Client disconnected from " + client.getInetAddress());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error: " + e);
                        flag = false;
                        try {
                            socket.close();
                            System.out.println("Client disconnected from " + client.getInetAddress());
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
}