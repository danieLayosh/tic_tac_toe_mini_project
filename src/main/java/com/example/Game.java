package com.example;

// import java.net.Socket;
import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;

import com.example.IProtocol.ICommunicationHandler;

// import javafx.application.Platform;

public class Game {
    private int boardSize;
    private int[][] board;
    private String player1;
    private String player2;
    private int playersJoined;
    // private Socket playerSocket1;
    // private Socket playerSocket2;
    private ICommunicationHandler socket1;
    private ICommunicationHandler socket2;

    private ExecutorService executorService;

    public Game(int boardSize, String player1, ICommunicationHandler handler) {
        this.boardSize = boardSize;
        this.player1 = player1;
        this.player2 = "Awaiting player 2";
        this.playersJoined = 1;
        this.setCommunicationHandler(handler);
        this.board = new int[boardSize][boardSize];
    }

    public void setCommunicationHandler(ICommunicationHandler handler) {
        if (this.socket1 == null) {
            this.socket1 = handler;
        } else if (this.socket2 == null) {
            this.socket2 = handler;
            this.playersJoined = 2;
            this.player2 = "Player 2 Joined"; 
        }
    }
    
    

    public void changeBoard(int x, int y, int value) {

        board[x][y] = value;

        // for (int i = 0; i < boardSize; i++) {
        //     for (int j = 0; j < boardSize; j++) {
        //         System.out.print(board[i][j] + " ");
        //     }
        //     System.out.println();
        // }

        checkWin();

        try {
            socket1.send("refreshBoard " + x + " " + y + " " + value);
            socket2.send("refreshBoard " + x + " " + y + " " + value);
            switchBoard(value);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }

    private void checkWin() {
        int size = this.boardSize;
        int board[][] = this.board;
        int value = 0;
        boolean win = false;

        // checks rows
        for (int i = 0; i < boardSize; i++) {
            value = board[i][0];
            if (value == 0) {
                continue;
            }
            win = true;

            for (int j = 1; j < size; j++) {
                if (board[i][j] != value) {
                    win = false;
                    break;
                }
            }
            if (win) {
                break;
            }
        }
        if (win) {
            win(value);
            return;
        }

        // checkes col
        for (int i = 0; i < size; i++) {
            value = board[0][i];
            if (value == 0) {
                continue;
            }
            win = true;
            for (int j = 1; j < size; j++) {
                if (board[j][i] != value) {
                    win = false;
                    break;
                }
            }
            if (win) {
                break;
            }
        }
        if (win) {
            win(value);
            return;
        }

        // checks diagonal left to right
        value = board[0][0];
        if (value != 0) {
            win = true;
            for (int i = 1; i < size; i++) {
                if (board[i][i] != value) {
                    win = false;
                    break;
                }
            }
            if (win) {
                win(value);
                return;
            }
        }
        // checks diagonal right to left
        value = board[0][size - 1];
        if (value != 0) {
            win = true;
            for (int i = 1; i < size; i++) {
                if (board[i][size - 1 - i] != value) {
                    win = false;
                    break;
                }
            }
            if (win) {
                win(value);
                return;
            }
        }

        // checks draw
        for (int i = 0; i < size; i++)
            for (int h = 0; h < size; h++)
                if (board[i][h] == 0)
                    return;
        draw();
    }

    private void win(int value) {
        try {
            if (value == 1) {
                socket1.send("win");
                socket2.send("lose");
            } else {
                socket1.send("lose");
                socket2.send("win");
            }
            shutdown();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    void switchBoard(int value) {
        try {
            if (value == 1) {
                socket1.send("disableBoard");
                socket1.send("not_your_turn");
                socket2.send("enableBoard");
                socket2.send("your_turn");
            } else {
                socket1.send("enableBoard");
                socket1.send("your_turn");
                socket2.send("disableBoard");
                socket2.send("not_your_turn");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public int getBoard(int i, int j) {
        return board[i][j];
    }

    public void updateGUI() {
        try {
            // socket1.send("getPlayers" + player1 + " " + player2);
            // socket2.send("getPlayers" + player1 + " " + player2);
            socket1.send("opponentname" + " " + player2);
            socket2.send("opponentname" + " " + player1);
            socket1.send("isFull true");
            socket2.send("isFull true");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void draw() {
        try {
            socket1.send("draw");
            socket2.send("draw");
            shutdown();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
        this.playersJoined = 2;
        try {
            socket1.send("createBoard");
            socket2.send("createBoard");
            updateGUI();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    public boolean isFull() {
        return playersJoined == 2;
    }

    public String[] getPlayers() {
        return new String[] { player1, player2 };
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void close() {
        try {
            if (socket1 != null) {
                socket1.close();
            }
            if (socket2 != null) {
                socket2.close();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
