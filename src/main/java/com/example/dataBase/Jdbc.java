package com.example.dataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Jdbc {

    private static final String DATABASE_URL = "jdbc:mysql://localhost/Tic_tac_toe_games";
    private static final String DATABASE_DRIVER = "com.mysql.cj.jdbc.Driver";
  
    public Jdbc(){
        try {
            // This line loads the JDBC driver class
            Class.forName(DATABASE_DRIVER);
            Connection connection = DriverManager.getConnection(DATABASE_URL, "root", "tt112233"); // can add user and password if needed
            Statement stmt = connection.createStatement();

            ResultSet resultSet = stmt.executeQuery("select gameId, player1, player2, result, winner, boardSize, startTime, endTime from games");

            while (resultSet.next()) {
                System.out.println(resultSet.getString("gameId") + " -> {" +
                        resultSet.getString(2) + ", " + resultSet.getString(3) + ", " +
                        resultSet.getString(4) + ", " + resultSet.getString(5) + ", " +
                        resultSet.getString(6) + ", " + resultSet.getString(7) + ", " +
                        resultSet.getString(8) + "} ");
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
