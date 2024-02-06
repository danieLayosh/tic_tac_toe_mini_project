package com.example.dataBase.Model;

import java.sql.Timestamp;

public class GameModel extends BaseEntity {
    public enum Result {
        WIN, LOSE, DRAW
    }

    private PlayerModel player1;
    private PlayerModel player2;
    private PlayerModel winner;
    private int boardSize;
    private Timestamp startTime;
    private Timestamp endTime;
    private Result result;

    public PlayerModel getPlayer1() {
        return player1;
    }

    public void setPlayer1(PlayerModel player1) {
        this.player1 = player1;
    }

    public PlayerModel getPlayer2() {
        return player2;
    }

    public void setPlayer2(PlayerModel player2) {
        this.player2 = player2;
    }

    public PlayerModel getWinner() {
        return (winner != null) ? winner : null;
    }

    public void setWinner(PlayerModel winner) {
        this.winner = winner;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    };

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return super.getId() + " -> {" +
                this.player1.getPlayerName() + ", " + this.player2.getPlayerName() + ", " +
                this.result + ", " + ((winner != null) ? winner.getPlayerName() : "NO WINNER") + ", " +
                this.boardSize + ", " + this.getStartTime() + ", " +
                this.getEndTime() + "} ";
    }
}
