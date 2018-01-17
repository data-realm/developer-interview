package com.data_realm.interview.repository.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

    private Connection connection;
    private static DbConnection dbConnection;

    private DbConnection(){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:resources/data/citystatecountry.db");
            System.out.println("Opened database successfully - in singleton class");

        } catch (ClassNotFoundException cnf) {
            System.out.println("could not load driver");
        } catch (SQLException sqle) {
            System.out.println("sql exception:" + sqle.getStackTrace());
        }
    }

    public static DbConnection getInstance() {
        synchronized(DbConnection.class) {
            if (dbConnection == null) {
                dbConnection = new DbConnection();
            }
        }

        return dbConnection;
    }

    public Connection getConnection() {
        return connection;
    }
}
