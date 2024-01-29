package com.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;

public class Game {
    private int boardSize;
    private int[][] board;
    private String player1;
    private String player2;
    private int playersJoined;
    private GUI gui1;
    private GUI gui2;

    private ExecutorService executorService;

    public Game(int boardSize, GUI gui) {
        this.boardSize = boardSize;
        this.player1 = "player 1";
        this.player2 = "player 2";
        this.playersJoined = 1;
        this.board = new int[boardSize][boardSize];
        this.executorService = Executors.newFixedThreadPool(4);
    }

    public Game(int boardSize, String player1, GUI gui) {
        this.boardSize = boardSize;
        this.player1 = player1;
        this.player2 = "player 2";
        this.playersJoined = 1;
        this.setGUI(gui);
        this.board = new int[boardSize][boardSize];
        this.executorService = Executors.newFixedThreadPool(4);
    }

    public Game(int boardSize, String player1, String player2) {
        this.boardSize = boardSize;
        this.player1 = player1;
        this.player2 = player2;
        this.playersJoined = 2;
        this.board = new int[boardSize][boardSize];
        this.executorService = Executors.newFixedThreadPool(4);
    }

    public synchronized void changeBoard(int x, int y, int value) {
        executorService.execute(() -> {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            board[x][y] = value;
            checkWin();

            // Update UI after task completion
            Platform.runLater(() -> {
                gui1.updateBoard();
                gui2.updateBoard();
                switchBoard(value);
            });
        });
    }

    public void shutdown() {
        executorService.shutdown();
    }

    public void setGUI(GUI gui) {
        if (gui1 == null) {
            gui1 = gui;
        } else {
            gui2 = gui;
        }
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
        if (value == 1) {
            Platform.runLater(() -> {
                gui1.win();
                gui2.lose();
            });
        } else {
            Platform.runLater(() -> {
                gui1.lose();
                gui2.win();
            });
        }
        shutdown();
    }

    void switchBoard(int value) {
        if (value == 1) {
            Platform.runLater(() -> {
                gui1.disableBoard();
                gui2.enableBoard();
            });
        } else {
            Platform.runLater(() -> {
                gui1.enableBoard();
                gui2.disableBoard();
            });
        }
    }

    public int[][] getBoard() {
        return board;
    }

    public void updateGUI() {
        Platform.runLater(() -> {
            gui1.refresh();
            gui2.refresh();
        });
    }

    public void draw() {
        Platform.runLater(() -> {
            gui1.draw();
            gui2.draw();
        });
        shutdown();
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
        this.playersJoined = 2;
        if (boardSize > 20) {
            Platform.runLater(() -> {
                gui1.createBoard();
                gui2.createBoard();
                updateGUI();
            });
        } else {
            gui1.createBoard();
            gui2.createBoard();
            updateGUI();
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

}
