package com.example.dataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Jdbc {

    private static final String URL_PATH = "jdbc:mysql://localhost/Tic_tac_toe_games";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String username = "root";
    private static final String password = "tt112233";

    private Connection connection;
    private Statement stmt;
    private ResultSet res;

    public Jdbc(){
        try {
            // This line loads the JDBC driver class
            Class.forName(DRIVER_CLASS);
            connection = DriverManager.getConnection(URL_PATH, username, password);
            stmt = connection.createStatement();
            
            res = stmt.executeQuery("select gameId, player1, player2, winner, boardSize, result, startTime, endTime from games");

            while (res.next()) {
                System.out.println(res.getString("gameId") + " -> {" +
                        res.getString(2) + ", " + res.getString(3) + ", " +
                        res.getString(4) + ", " + res.getString(5) + ", " +
                        res.getString(6) + ", " + res.getString(7) + ", " +
                        res.getString(8) + "} ");
            }

            connection.close();

        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    
}
