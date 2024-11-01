package com.move.model.dao;

import com.move.resource.ConnectionDatabase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ExchangeRatesDao {

  public ResultSet getExchangeRatesFromDB() {
    String sqlQuery = "select * from exchange_rates;";
    Connection connection;
    Statement statement;
    ResultSet resultSet;
    try {
      Class.forName("org.sqlite.JDBC");
      connection = ConnectionDatabase.getConnection();
      statement = connection.createStatement();
      resultSet = statement.executeQuery(sqlQuery);
    } catch (ClassNotFoundException | SQLException e) {
      throw new RuntimeException(e);
    }
    return resultSet;
  }

}
