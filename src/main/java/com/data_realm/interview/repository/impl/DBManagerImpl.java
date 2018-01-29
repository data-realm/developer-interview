package com.data_realm.interview.repository.impl;

import com.data_realm.interview.StatsRetrivalError;
import com.data_realm.interview.model.CountryPopulation;
import com.data_realm.interview.repository.DBManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * This DBManager implementation provides a connection to the database containing population data.
 *
 * Created by ckeswani on 9/16/15.
 */
@Repository("database-stats")
public class DBManagerImpl implements DBManager {
    private Connection connection = null;

    public DBManagerImpl() {
        try {
            if(connection == null)
                connection = setupHikari().getConnection();

            System.out.println("Opened database successfully");
        } catch (SQLException sqle) {
            System.out.println("sql exception:" + sqle.getStackTrace());
        } catch (Exception e) {
            System.out.println("error occurred while connecting to db exception:" + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public List<CountryPopulation> getCountryPopulations() throws StatsRetrivalError {
      String select = "SELECT c.CountryName, sum(ci.Population) AS 'Population' " +
              "FROM Country c " +
              "LEFT JOIN State s ON c.CountryId = s.CountryId " +
              "LEFT JOIN City ci ON s.StateId = ci.StateId " +
              "GROUP BY c.CountryName ASC";
      return runQuery(select);
    }

    @Override
    public Optional<CountryPopulation> getCountryPopulationByName(String countryName) throws IOException {
        String select = "SELECT c.CountryName, sum(ci.Population) AS 'Population' " +
                "FROM Country c " +
                "LEFT JOIN State s ON c.CountryId = s.CountryId " +
                "LEFT JOIN City ci ON s.StateId = ci.StateId " +
                "WHERE LOWER(c.CountryName) = '" + countryName + "' " +
                "GROUP BY c.CountryName ASC";

        return runQuery(select).stream().findFirst();
    }

  private List<CountryPopulation> runQuery(String query) throws StatsRetrivalError {
      List<CountryPopulation> results = new ArrayList<>();
      try {
          Statement statement = getConnection().createStatement();
          if (statement.execute(query)) {
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

    /**
     * Hikari will mange your DB threads
     * You can also add Hibernate if the thins get larger
     * At this scale you don't need it, beside that if you use an ORM you don't have fully control over the SQL
     */
  private HikariDataSource setupHikari() throws SQLException {
      HikariConfig config = new HikariConfig();

      config.setJdbcUrl("jdbc:sqlite:resources/data/citystatecountry.db");
      config.setConnectionTimeout(2000); //miliseconds
      config.setMaximumPoolSize(10);


      return new HikariDataSource(config);
  }
}
