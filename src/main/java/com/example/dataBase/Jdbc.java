package com.example.dataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Jdbc {

    private static final String DATABASE_URL = "jdbc:mysql://localhost/mydb";
    private static final String DATABASE_DRIVER = "com.mysql.cj.jdbc.Driver";
  
    public static void main(String[] args){
        try {
            // This line loads the JDBC driver class
            Class.forName(DATABASE_DRIVER);
            Connection connection = DriverManager.getConnection(DATABASE_URL, "root", "tt112233"); // can add user and password if needed
            Statement stmt = connection.createStatement();

            ResultSet resultSet = stmt.executeQuery("select PersonID, FirstName, LastName from persons");

            while (resultSet.next()) {
                System.out.println(resultSet.getString("PersonID") + " " +
                        resultSet.getString(2) + " " + resultSet.getString(3));
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
