package com.move.model.dao;

import com.move.resource.ConnectionDatabase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ExchangeRatesDao {

  private Connection connection;

  public ExchangeRatesDao() {
    connection = ConnectionDatabase.getConnection();
  }

  public ResultSet findExchangeRatesFromDB() {
    String sqlQuery = "select * from exchange_rates;";
    Statement statement;
    ResultSet resultSet;
    try {
      statement = connection.createStatement();
      resultSet = statement.executeQuery(sqlQuery);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return resultSet;
  }

}
