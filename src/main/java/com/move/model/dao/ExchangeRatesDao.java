package com.move.model.dao;

import com.move.dto.ExchangeRateDto;
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

  public boolean deleteExchangeRateByCurrencyCodesFromDB(int baseCurrencyId, int targetCurrencyId) {
    String getIdSqlQuery = "SELECT id FROM exchange_rates WHERE base_currency_id = (?) AND target_currency_id = (?);";
    String deleteSqlQuery = "DELETE FROM exchange_rates WHERE id = (?);";
    String updateIdsSqlQuery = "UPDATE exchange_rates SET id = id - 1 WHERE id > (?);";
    String resetAutoincrementSqlQuery = "UPDATE sqlite_sequence SET seq = (SELECT MAX(id) FROM exchange_rates) WHERE name = 'exchange_rates';";

    boolean isExchangeRateDeleted = false;
    int deletedId = -1;

    try {
      PreparedStatement getIdStatement = connection.prepareStatement(getIdSqlQuery);
      getIdStatement.setInt(1, baseCurrencyId);
      getIdStatement.setInt(2, targetCurrencyId);

      ResultSet resultSet = getIdStatement.executeQuery();
      if (resultSet.next()) {
        deletedId = resultSet.getInt("id");
      }
      if (deletedId != -1) {
        PreparedStatement deleteStatement = connection.prepareStatement(deleteSqlQuery);
        deleteStatement.setInt(1, deletedId);
        int isQueryExecuted = deleteStatement.executeUpdate();
        if (isQueryExecuted != 0) {
          PreparedStatement updateStatement = connection.prepareStatement(updateIdsSqlQuery);
          updateStatement.setInt(1, deletedId);
          updateStatement.executeUpdate();
          Statement resetStatement = connection.createStatement();
          resetStatement.executeUpdate(resetAutoincrementSqlQuery);
          isExchangeRateDeleted = true;
        }
      }
      connection.commit();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return isExchangeRateDeleted;
  }


}
