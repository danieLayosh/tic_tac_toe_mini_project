package com.example;

import java.util.ArrayList;

import java.net.ServerSocket;
import java.net.Socket;

public class Host {
    public static void main(String[] args) {
        try {
            int port = ProtocolS.getProt(); // Make sure this is the port you want to use
            ServerSocket server = new ServerSocket();
            server.bind(new java.net.InetSocketAddress(ProtocolS.getHost(), port));
            System.out.println("Server started on port 1234");
            ArrayList<Game> games = new ArrayList<Game>();
            while (true) {
                Socket client = server.accept();
                ProtocolS sock = new ProtocolS(client);
                System.out.println("Client connected from " + client.getInetAddress());
                new Thread(() -> {
                    Boolean flag = true;
                    try {
                        String msg = sock.res();
                        String name = msg.split(" ")[0];
                        String size = msg.split(" ")[1];
                        Game currentgame = null;
                        for (Game game : games) {
                            if (game.getBoardSize() == Integer.parseInt(size) && !game.isFull()) {
                                sock.send("2");
                                game.setSocket(client);
                                game.setPlayer2(name);
                                currentgame = game;
                                games.remove(game);
                                break;
                            }
                        }
                        if (currentgame == null) {
                            sock.send("1");
                            currentgame = new Game(Integer.parseInt(size), name, client);
                        }
                        games.add(currentgame);
                        System.out.println("Game created with size " + size);
                        while (flag && !client.isClosed()) {
                            msg = sock.res();
                            String[] msgArr = msg.split(" ");
                            if (msgArr[0].equals("isFull")) {
                                sock.send("isFull " + String.valueOf(currentgame.isFull()));
                            } else if (msgArr[0].equals("getBoardSize")) {
                                sock.send(String.valueOf(currentgame.getBoardSize()));
                            } else if (msgArr[0].equals("changeBoard")) {
                                currentgame.changeBoard(Integer.parseInt(msgArr[1]), Integer.parseInt(msgArr[2]),
                                        Integer.parseInt(msgArr[3]));
                            } else if (msgArr[0].equals("getBoard")) {
                                sock.send(String.valueOf(
                                        currentgame.getBoard(Integer.parseInt(msgArr[1]),
                                                Integer.parseInt(msgArr[2]))));
                            } else if (msgArr[0].equals("getPlayers")) {
                                String[] players = currentgame.getPlayers();
                                sock.send("getPlayers " + players[0] + " " + players[1]);
                            } else if (msgArr[0].equals("close")) {
                                currentgame.close();
                                games.remove(currentgame);
                                sock.close();
                                flag = false;
                                System.out.println("Client disconnected from " + client.getInetAddress());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error: " + e);
                        flag = false;
                        try {
                            sock.close();
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