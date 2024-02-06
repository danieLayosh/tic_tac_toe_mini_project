package com.example.dataBase.ViewModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.example.dataBase.Model.BaseEntity;
import com.example.dataBase.Model.GameModel;
import com.example.dataBase.Model.PlayerModel;
import com.example.dataBase.Model.Collection.GameList;
import com.example.dataBase.Model.Collection.PlayerList;

public class GameDB extends BaseDB {

    public GameDB() {
        super();
    }

    @Override
    protected BaseEntity newEntity() {
        return new GameModel();
    }

    public GameList selectAll() {
        String sqlStr = "SELECT * FROM games";
        return new GameList(super.select(sqlStr));
    }

    public GameList selectGameByPlayerName(String name) {
        String sqlStr = "SELECT * FROM games WHERE player1 = '" + name + "' OR player2 = '" + name + "'";
        List<BaseEntity> lst = super.select(sqlStr);
        GameList list = new GameList();
        lst.forEach(ent -> list.add((GameModel) ent));
        return list;
    }

    public GameList selectByGameID(int id) {
        String sqlStr = "SELECT * FROM games WHERE gameId = " + id;
        List<BaseEntity> lst = super.select(sqlStr);
        GameList list = new GameList();
        lst.forEach(ent -> list.add((GameModel) ent));
        return list;
    }

    public GameList selectByBoardSize(int size) {
        String sqlStr = "SELECT * FROM games WHERE boardSize = " + size;
        List<BaseEntity> lst = super.select(sqlStr);
        GameList list = new GameList();
        lst.forEach(ent -> list.add((GameModel) ent));
        return list;
    }

    public GameList selectWinGame() {
        String sqlStr = "SELECT * FROM games WHERE result = 'WIN'";
        List<BaseEntity> lst = super.select(sqlStr);
        GameList list = new GameList();
        lst.forEach(ent -> list.add((GameModel) ent));
        return list;
    }

    public GameList selectDrawGame() {
        String sqlStr = "SELECT * FROM games WHERE result = 'DRAW'";
        List<BaseEntity> lst = super.select(sqlStr);
        GameList list = new GameList();
        lst.forEach(ent -> list.add((GameModel) ent));
        return list;
    }

    public int insert(GameModel game) {
        String sqlStr = "INSERT INTO games (player1, player2, winner, boardSize, result, startTime, endTime) VALUES ('"
                + game.getPlayer1().getPlayerName() + "', '" + game.getPlayer2().getPlayerName() + "', '"
                + game.getWinner().getPlayerName() + "', " + game.getBoardSize() + ", '" + game.getResult().toString()
                + "', '" + game.getStartTime() + "', '" + game.getEndTime() + "')";
        return super.saveChanges(sqlStr);
    }

    public int update(GameModel game) {
        String sqlStr = "UPDATE games SET player1 = '" + game.getPlayer1().getPlayerName() + "', player2 = '"
                + game.getPlayer2().getPlayerName() + "', winner = '" + game.getWinner().getPlayerName()
                + "', boardSize = "
                + game.getBoardSize() + ", result = '" + game.getResult().toString() + "', startTime = '"
                + game.getStartTime() + "', endTime = '" + game.getEndTime() + "' WHERE gameId = " + game.getId();
        return super.saveChanges(sqlStr);
    }

    public int delete(int id) {
        String sqlStr = "DELETE FROM games WHERE gameId = " + id;
        return super.saveChanges(sqlStr);
    }

    public int delete(GameModel game) {
        return delete(game.getId());
    }

    protected GameModel createModel(GameModel game, ResultSet res) throws SQLException {
        game.setId(res.getInt("gameId"));
        String player1Name = res.getString("player1");
        String player2Name = res.getString("player2");
        String winnerName = res.getString("winner");

        try {
            game.setPlayer1(new PlayerDB().selectPlayerByName(player1Name));
        } catch (IndexOutOfBoundsException e) {
            game.setPlayer1(null); // Handle case when player is not found
        }

        try {
            game.setPlayer2(new PlayerDB().selectPlayerByName(player2Name));
        } catch (IndexOutOfBoundsException e) {
            game.setPlayer2(null); // Handle case when player is not found
        }

        if (winnerName == null || winnerName.isEmpty()) {
            game.setWinner(null);
        } else {
            try {
                game.setWinner(new PlayerDB().selectPlayerByName(winnerName));
            } catch (IndexOutOfBoundsException e) {
                game.setWinner(null); // Handle case when winner is not found
            }
        }

        game.setBoardSize(res.getInt("boardSize"));
        String resultString = res.getString("result");
        GameModel.Result result = GameModel.Result.valueOf(resultString.toUpperCase());
        game.setResult(result);
        game.setStartTime(res.getTimestamp("startTime"));
        game.setEndTime(res.getTimestamp("endTime"));
        return game;
    }

    @Override
    protected BaseEntity createModel(BaseEntity entity, ResultSet res) throws SQLException {
        if (entity instanceof GameModel) {
            return createModel((GameModel) entity, res);
        }
        return null;
    }
}
