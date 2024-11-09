package com.move.exchangeRate.dao;

import com.move.exchangeRate.dto.ExchangeRateDto;
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

  public ResultSet findExchangeRateByCurrenciesId(int baseCurrencyId, int targetCurrencyId) {
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


  public ResultSet addExchangeRateToDB(ExchangeRateDto exchangeRateDto) {
    ResultSet resultSet = null;
    String sqlQuery = "insert into exchange_rates(base_currency_id, target_currency_id, rate) values (?, ?, ?);";
    int baseCurrencyId = exchangeRateDto.baseCurrencyId();
    int targetCurrencyId = exchangeRateDto.targetCurrencyId();
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
      preparedStatement.setInt(1, baseCurrencyId);
      preparedStatement.setInt(2, targetCurrencyId);
      preparedStatement.setBigDecimal(3, exchangeRateDto.rate());
      int isQueryExecuted = preparedStatement.executeUpdate();
      if (isQueryExecuted != 0) {
        resultSet = findExchangeRateByCurrenciesId(baseCurrencyId, targetCurrencyId);
      }
      connection.commit();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return resultSet;
  }

  public ResultSet updateExchangeRateInDB(ExchangeRateDto exchangeRateDto) throws SQLException {
    ResultSet resultSet = null;
    String sqlQuery = "update exchange_rates set rate = (?) where base_currency_id = (?) and target_currency_id = (?);";
    int baseCurrencyId = exchangeRateDto.baseCurrencyId();
    int targetCurrencyId = exchangeRateDto.targetCurrencyId();
    PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
    preparedStatement.setBigDecimal(1, exchangeRateDto.rate());
    preparedStatement.setInt(2, baseCurrencyId);
    preparedStatement.setInt(3, targetCurrencyId);
    int isQueryExecuted = preparedStatement.executeUpdate();
    if (isQueryExecuted != 0) {
      resultSet = findExchangeRateByCurrenciesId(baseCurrencyId, targetCurrencyId);
    }
    connection.commit();
    return resultSet;
  }

  public boolean deleteExchangeRateByCurrencyCodesFromDB(int id) {
    String deleteSqlQuery = "DELETE FROM exchange_rates WHERE id = (?);";
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(deleteSqlQuery);
      preparedStatement.setInt(1, id);
      preparedStatement.executeUpdate();
      connection.commit();
      return true;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }


}
