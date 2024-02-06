package com.example.dataBase.ViewModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.example.dataBase.Model.BaseEntity;
import com.example.dataBase.Model.PlayerModel;
import com.example.dataBase.Model.Collection.PlayerList;

public class PlayerDB extends BaseDB {

    public PlayerDB() {
        super();
    }

    @Override
    protected BaseEntity newEntity() {
        return new PlayerModel();
    }

    protected PlayerModel createModel(PlayerModel player, ResultSet res) throws SQLException {
        player.setPlayerName(res.getString("playerName"));
        return player;
    }

    @Override
    protected BaseEntity createModel(BaseEntity entity, ResultSet res) throws SQLException {
        if (entity instanceof PlayerModel) {
            return createModel((PlayerModel) entity, res);
        }
        return null; // Or handle appropriately
    }

    public PlayerList selectAll() {
        String sqlStr = "SELECT * FROM players";
        return new PlayerList(super.select(sqlStr));
    }

    public PlayerList selectByName(String name) {
        String sqlStr = "SELECT * FROM players WHERE playerName = '" + name + "'";
        List<BaseEntity> lst = super.select(sqlStr);
        PlayerList list = new PlayerList();
        lst.forEach(ent -> list.add((PlayerModel) ent));
        return list;
    }

    public PlayerModel selectPlayerByName(String name) {
        return selectByName(name).get(0);
    }

    public int insert(PlayerModel player) {
        String sqlStr = "INSERT INTO players (playerName) VALUES ('" + player.getPlayerName() + "')";
        return super.saveChanges(sqlStr);
    }

    public int update(PlayerModel player, String newName) {
        String sqlStr = "UPDATE players SET playerName = '" + newName + "' WHERE playerName = '"
                + player.getPlayerName() + "'";
        return super.saveChanges(sqlStr);
    }

    public int delete(PlayerModel player) {
        String sqlStr = "DELETE FROM players WHERE playerName = '" + player.getPlayerName() + "'";
        return super.saveChanges(sqlStr);
    }

}
