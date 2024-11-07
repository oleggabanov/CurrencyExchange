package com.move.model.dao;

import com.move.resource.ConnectionDatabase;

import java.sql.*;

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

  public ResultSet findExchangeRateByCurrencyId(int baseCurrencyId, int targetCurrencyId) {
    String sqlQuery = "select * from exchange_rates where base_currency_id = (?) and target_currency_id = (?);";
    ResultSet resultSet;
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
      preparedStatement.setInt(1, baseCurrencyId);
      preparedStatement.setInt(2, targetCurrencyId);
      resultSet = preparedStatement.executeQuery();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    return resultSet;
  }


}
