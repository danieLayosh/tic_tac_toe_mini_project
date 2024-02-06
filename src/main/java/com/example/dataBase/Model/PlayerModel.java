package com.example.dataBase.Model;

public class PlayerModel extends BaseEntity {
    // private int id
    private String playerName;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public String toString() {
        return playerName;
    }
}
