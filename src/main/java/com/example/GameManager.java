package com.example;

import java.sql.Timestamp;

import com.example.IProtocol.ICommunicationHandler;
import com.example.dataBase.Model.GameModel;
import com.example.dataBase.Model.PlayerModel;
import com.example.dataBase.ViewModel.GameDB;
import com.example.dataBase.ViewModel.PlayerDB;

public class GameManager {
    private int id;
    private int boardSize;
    private int[][] board;
    private String player1;
    private String player2;
    private int playersJoined;
    private ICommunicationHandler handler1;
    private ICommunicationHandler handler2;
    private GameModel gameModel;
    private PlayerModel player1Model;
    private PlayerModel player2Model;

    private static final GameDB gameDB = new GameDB();
    private static final PlayerDB playerDB = new PlayerDB();

    public GameManager(int boardSize, String player1, ICommunicationHandler handler1) {
        this.boardSize = boardSize;
        this.player1 = player1;
        handler1.send("showPlayerName" + " " + player1);
        this.player2 = "player2";
        this.playersJoined = 1;
        this.setCommunicationHandler(handler1);
        this.board = new int[boardSize][boardSize];

        player1Model = new PlayerModel(player1);
        player2Model = new PlayerModel(player2);
        if (playerDB.selectByName(player1).size() == 0) {
            playerDB.insert(player1Model);
        } else {
            player1Model = playerDB.selectByName(player1).get(0);
        }
        playerDB.insert(player2Model);
        playerDB.saveChanges();

        this.gameModel = new GameModel(player1Model, player2Model, boardSize,
                Timestamp.class.cast(new Timestamp(System.currentTimeMillis())));
        gameModel.setResult(GameModel.Result.ACTIVE);
        gameDB.insert(gameModel);
        gameDB.saveChanges();
        this.setId(gameModel.getId());
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

        checkWin();

        try {
            handler1.send("refreshBoard " + x + " " + y + " " + value);
            handler2.send("refreshBoard " + x + " " + y + " " + value);
            switchBoard(value);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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
        gameModel.setResult(GameModel.Result.WIN);
        gameModel.setEndTime(Timestamp.class.cast(new Timestamp(System.currentTimeMillis())));
        // gameDB.update(gameModel);
        // gameDB.saveChanges();
        try {
            if (value == 1) {
                handler1.send("win");
                handler2.send("lose");
                gameModel.setWinner(player1Model);
            } else {
                handler1.send("lose");
                handler2.send("win");
                gameModel.setWinner(player2Model);
            }
            gameDB.update(gameModel);
            gameDB.saveChanges();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void draw() {
        this.gameModel.setResult(GameModel.Result.DRAW);
        this.gameModel.setEndTime(Timestamp.class.cast(new Timestamp(System.currentTimeMillis())));
        gameDB.update(gameModel);
        gameDB.saveChanges();
        try {
            handler1.send("draw");
            handler2.send("draw");
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

    public void setPlayer2(String player2) {
        this.player2 = player2;
        this.playersJoined = 2;
        PlayerModel p2Model = playerDB.selectByName(player2).get(0);
        if (p2Model == null) {
            System.out.println("Player 2 does not exist");
            playerDB.update(player2Model, player2);
            gameModel.setPlayer2(player2Model);

        } else {
            System.out.println("Player 2 already exists");
            // playerDB.update(player2Model, player2);
            // playerDB.saveChanges();
            gameModel.setPlayer2(p2Model);
            gameDB.update(gameModel);
            gameDB.saveChanges();

            playerDB.delete(player2Model);
        }

        playerDB.saveChanges();

        // player2Model.setPlayerName(player2);
        gameModel.setStartTime(Timestamp.class.cast(new Timestamp(System.currentTimeMillis())));
        gameDB.update(gameModel);
        gameDB.saveChanges();

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
