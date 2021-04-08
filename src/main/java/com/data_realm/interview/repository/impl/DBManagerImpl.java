package com.data_realm.interview.repository.impl;

import com.data_realm.interview.StatsRetrivalError;
import com.data_realm.interview.model.CountryPopulation;
import com.data_realm.interview.repository.DBManager;

import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * This DBManager implementation provides a connection to the database containing population data.
 */
@Repository("database-stats")
public class DBManagerImpl implements DBManager {
  private static final String SELECT_ALL = "SELECT c.CountryName, sum(ci.Population) AS 'Population' " +
      "FROM Country c " +
      "INNER JOIN State s ON c.CountryId = s.CountryId " +
      "INNER JOIN City ci ON s.StateId = ci.StateId " +
      "GROUP BY c.CountryName";

    public Connection getConnection() {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:resources/data/citystatecountry.db");
            System.out.println("Opened database successfully");

        } catch (ClassNotFoundException cnf) {
            System.out.println("could not load driver");
        } catch (SQLException sqle) {
            System.out.println("sql exception:" + sqle.getStackTrace());
        }
        return c;
    }

  @Override
  public List<CountryPopulation> getCountryPopulations() throws StatsRetrivalError {
    List<CountryPopulation> results = new ArrayList<>();
    try {
      Statement statement = getConnection().createStatement();
      if (statement.execute(SELECT_ALL)) {
        ResultSet resultSet = statement.getResultSet();
        while (resultSet.next()) {
          results.add(new CountryPopulation(resultSet.getString(1), resultSet.getInt(2)));
        }
      }
    } catch (SQLException e) {
      System.out.println("e = " + e);
      throw new StatsRetrivalError();
    }

    return results;
  }
}
