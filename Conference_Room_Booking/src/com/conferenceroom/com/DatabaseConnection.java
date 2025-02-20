package com.conferenceroom.com;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/conference";
    private static final String USER = "root";
    private static final String PASSWORD = "12345";

    // Private constructor to prevent instantiation
    private DatabaseConnection() {}

    // Method to get a new connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}