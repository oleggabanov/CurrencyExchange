package com.move.dao;

import com.move.dto.CurrencyDto;
import com.move.resource.ConnectionDatabase;

import java.sql.*;

public class CurrenciesDao {

  private Connection connection;

  public CurrenciesDao() {
    connection = ConnectionDatabase.getConnection();
  }

  public ResultSet findCurrenciesFromDB() {
    String sqlQuery = "select * from currencies;";
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

  public ResultSet findCurrencyByCodeFromDB(String currencyCode) {
    ResultSet resultSet;
    try {
      PreparedStatement preparedStatement = connection.prepareStatement("select * from currencies where code = (?);");
      preparedStatement.setString(1, currencyCode);
      resultSet = preparedStatement.executeQuery();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    return resultSet;
  }

  public ResultSet addCurrencyToDB(CurrencyDto newCurrency) {
    ResultSet resultSet = null;
    String currencyCode = newCurrency.code();
    String fullName = newCurrency.fullName();
    String sign = newCurrency.sign();
    try {
      PreparedStatement preparedStatement = connection
              .prepareStatement("insert into currencies (code, full_name, sign) values (?,?,?);");
      preparedStatement.setString(1, currencyCode);
      preparedStatement.setString(2, fullName);
      preparedStatement.setString(3, sign);
      int isCurrencyAdded = preparedStatement.executeUpdate();
      if (isCurrencyAdded != 0) {
        resultSet = findCurrencyByCodeFromDB(currencyCode);
      }
      connection.commit();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return resultSet;
  }

  public ResultSet findCurrencyByIdFromDB(int currencyId) {
    ResultSet resultSet;
    try {
      PreparedStatement preparedStatement = connection.prepareStatement("select * from currencies where id = (?);");
      preparedStatement.setInt(1, currencyId);
      resultSet = preparedStatement.executeQuery();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return resultSet;
  }

}
