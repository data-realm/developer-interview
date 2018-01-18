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
 *
 * Created by ckeswani on 9/16/15.
 */
@Repository("database-stats")
public class DBManagerImpl implements DBManager {
    /** ISSUE #3 - part 1/2 - countries with 0 population in the DB are also displayed */
    // This can help identifying possible issues that might occur for any country.
  private static final String SELECT_ALL = "SELECT c.CountryName, sum(ci.Population) AS 'Population' " +
      "FROM Country c " +
      "LEFT JOIN State s ON c.CountryId = s.CountryId " +
      "LEFT JOIN City ci ON s.StateId = ci.StateId " +
      "GROUP BY c.CountryName ORDER BY c.CountryName ASC";

    public Connection getConnection() {
        /** ISSUE #1 - can be related with the fact that every call of any endpoint of the API was opening an extra connection
         * to the database. The current solution is to open only a connection to the database so each request would not have
         * to wait for this operation each time. Since the current requests are not time expensive, this can be used without
         * any problems. If the requests would be more time consuming (like more API endpoinds being added in the future)
         * a connection pool setup would be highly recommended. A connection pool could be implemented using JDBC using
         * the same app architecture (for example org.apache.tomcat.jdbc.pool),
         * or using an ORM which would be a better option.*/
        return DbConnection.getInstance().getConnection();
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
