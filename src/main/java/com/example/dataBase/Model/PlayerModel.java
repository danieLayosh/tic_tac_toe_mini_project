package com.example.dataBase.Model;

public class PlayerModel extends BaseEntity {
    private String playerName;

    public PlayerModel(String playerName) {
        this.playerName = playerName;
    }

    public PlayerModel() {
        super();
    }

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
