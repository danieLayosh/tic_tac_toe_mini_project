package com.example.dataBase.ViewModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.example.dataBase.Model.BaseEntity;
import com.example.dataBase.Model.GameModel;
import com.example.dataBase.Model.Collection.GameList;
import com.example.dataBase.Model.Collection.PlayerList;

public class GameDB extends BaseDB {
    private static final String URL_PATH = "jdbc:mysql://localhost/Tic_tac_toe_games";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String username = "root";
    private static final String password = "tt112233";

    private Connection connection;
    private Statement stmt;
    // private ResultSet res;

    public GameDB() {
        super();
        try {
            // Loading driver
            Class.forName(DRIVER_CLASS);
            // Connecting database...
            connection = DriverManager.getConnection(URL_PATH, username, password);
            stmt = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    protected GameModel createModel(GameModel game, ResultSet res) throws SQLException {
        game.setId(res.getInt("gameId"));
        String player1Name = res.getString("player1");
        String player2Name = res.getString("player2");
        String winnerName = res.getString("winner");
        PlayerList player1List = new PlayerDB().selectByName(player1Name);
        if (!player1List.isEmpty()) {
            game.setPlayer1(player1List.get(0));
        } else {
            game.setPlayer1(null);
        }

        PlayerList player2List = new PlayerDB().selectByName(player2Name);
        if (!player2List.isEmpty()) {
            game.setPlayer2(player2List.get(0));
        } else {
            game.setPlayer2(null);
        }

        if (winnerName == null || winnerName.isEmpty()) {
            game.setWinner(null);
            return game;
        } else {
            PlayerList winnerList = new PlayerDB().selectByName(winnerName);
            if (!winnerList.isEmpty()) {
                game.setWinner(winnerList.get(0));
            } else {
                game.setWinner(null);
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
