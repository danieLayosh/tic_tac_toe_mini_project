package com.example;

// import java.net.Socket;
import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;

import com.example.IProtocol.ICommunicationHandler;

// import javafx.application.Platform;

public class GameManager {
    private int boardSize;
    private int[][] board;
    private String player1;
    private String player2;
    private int playersJoined;
    private ICommunicationHandler handler1;
    private ICommunicationHandler handler2;

    private ExecutorService executorService;

    public GameManager(int boardSize, String player1, ICommunicationHandler handler1) {
        this.boardSize = boardSize;
        this.player1 = player1;
        handler1.send("showPlayerName" + " " + player1);
        this.player2 = "Awaiting player 2";
        this.playersJoined = 1;
        this.setCommunicationHandler(handler1);
        this.board = new int[boardSize][boardSize];
    }

    public void setCommunicationHandler(ICommunicationHandler handler) {
        if (this.handler1 == null) {
            this.handler1 = handler;
        } else if (this.handler2 == null) {
            this.handler2 = handler;
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
            handler1.send("refreshBoard " + x + " " + y + " " + value);
            handler2.send("refreshBoard " + x + " " + y + " " + value);
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
                handler1.send("win");
                handler2.send("lose");
            } else {
                handler1.send("lose");
                handler2.send("win");
            }
            shutdown();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    void switchBoard(int value) {
        try {
            if (value == 1) {
                handler1.send("disableBoard");
                handler1.send("not_your_turn");
                handler2.send("enableBoard");
                handler2.send("your_turn");
            } else {
                handler1.send("enableBoard");
                handler1.send("your_turn");
                handler2.send("disableBoard");
                handler2.send("not_your_turn");
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
            // handler1.send("getPlayers" + player1 + " " + player2);
            // handler2.send("getPlayers" + player1 + " " + player2);
            handler1.send("showPlayerName" + " " + player1);
            handler2.send("showPlayerName" + " " + player2);
            handler1.send("opponentname" + " " + player2);
            handler2.send("opponentname" + " " + player1);
            handler1.send("isFull true");
            handler2.send("isFull true");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void draw() {
        try {
            handler1.send("draw");
            handler2.send("draw");
            shutdown();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
        this.playersJoined = 2;
        try {
            handler1.send("createBoard");
            handler2.send("createBoard");
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
            if (handler1 != null) {
                handler1.close();
            }
            if (handler2 != null) {
                handler2.close();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
