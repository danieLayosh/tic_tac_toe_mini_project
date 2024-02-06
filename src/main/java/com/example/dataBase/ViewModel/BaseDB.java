package com.example.dataBase.ViewModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.example.dataBase.Model.BaseEntity;

public abstract class BaseDB extends BaseEntity{
    private static final String URL_PATH = "jdbc:mysql://localhost/Tic_tac_toe_games";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String username = "root";
    private static final String password = "tt112233";

    private Connection connection;
    private Statement stmt;
    protected ResultSet res;

    protected abstract BaseEntity createModel(BaseEntity entity, ResultSet res) throws SQLException;
    protected abstract BaseEntity newEntity();

    public BaseDB() {
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

    public ArrayList<BaseEntity> select(String sqlStr) {
        ArrayList<BaseEntity> list = new ArrayList<BaseEntity>();
        try (Statement stmt = connection.createStatement();
             ResultSet res = stmt.executeQuery(sqlStr)) {
            while (res.next()) {
                BaseEntity entity = newEntity();
                createModel(entity, res); // Now passing ResultSet as parameter
                list.add(entity);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }
    
}
