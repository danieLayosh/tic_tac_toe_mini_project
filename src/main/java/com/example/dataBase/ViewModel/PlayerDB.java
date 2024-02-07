package com.example.dataBase.ViewModel;

import java.sql.PreparedStatement;
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
        return null;
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

    @Override
    public String createInsertSql(BaseEntity entity) {
        String sqlStr = "";
        if (entity instanceof PlayerModel) {
            PlayerModel player = (PlayerModel) entity;
            sqlStr = "INSERT INTO players (playerName) VALUES ('" + player.getPlayerName() + "')";
        }
        return sqlStr;
    }

    public String createUpdateSql(BaseEntity entity, String name) {
        String sqlStr = "";
        if (entity instanceof PlayerModel) {
            PlayerModel player = (PlayerModel) entity;
            sqlStr = "UPDATE players SET playerName = '" + name + "' WHERE playerName = '"
                    + player.getPlayerName() + "'";
        }
        return sqlStr;
    }

    @Override
    public String createUpdateSql(BaseEntity entity) {
        String sqlStr = "";
        if (entity instanceof PlayerModel) {
            PlayerModel player = (PlayerModel) entity;
            sqlStr = "UPDATE players SET playerName = '" + player.getPlayerName() + "' WHERE playerName = '"
                    + player.getPlayerName() + "'";
        }
        return sqlStr;
    }

    @Override
    public String createDeleteSql(BaseEntity entity) {
        String sqlStr = "";
        if (entity instanceof PlayerModel) {
            PlayerModel player = (PlayerModel) entity;
            sqlStr = "DELETE FROM players WHERE playerName = '" + player.getPlayerName() + "'";
        }
        return sqlStr;
    }

    @Override
    public void insert(BaseEntity entity) {
        if (entity instanceof PlayerModel) {
            inserted.add(new ChangeEntity(entity, this::createInsertSql));
        }
    }

    @Override
    public void update(BaseEntity entity) {
        if (entity instanceof PlayerModel) {
            updated.add(new ChangeEntity(entity, this::createUpdateSql));
        }
    }

    public void update(PlayerModel player1, String string) {
        updated.add(new ChangeEntity(player1, (entity) -> createUpdateSql(entity, string)));
    }

    @Override
    public void delete(BaseEntity entity) {
        if (entity instanceof PlayerModel) {
            deleted.add(new ChangeEntity(entity, this::createDeleteSql));
        }
    }

    @Override
    protected BaseDB me() {
        return this;
    }

}
