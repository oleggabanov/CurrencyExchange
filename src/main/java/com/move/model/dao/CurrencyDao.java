package com.move.model.dao;

import com.move.dto.CurrencyDto;
import com.move.resource.ConnectionDatabase;

import java.sql.*;

public class CurrencyDao {

  private Connection connection;

  public CurrencyDao() {
    connection = ConnectionDatabase.getConnection();
  }

  public ResultSet findCurrenciesFromDB() {
    String sqlQuery = "select * from currencies;";
    Statement statement;
    ResultSet resultSet;
    try {
      connection.setAutoCommit(false);
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
      connection.setAutoCommit(false);
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
      connection.setAutoCommit(false);
      PreparedStatement preparedStatement = connection.prepareStatement("insert into currencies (code, full_name, sign) values (?,?,?);");
      preparedStatement.setString(1, currencyCode);
      preparedStatement.setString(2, fullName);
      preparedStatement.setString(3, sign);
      int isCurrencyAdded = preparedStatement.executeUpdate();
      preparedStatement.executeQuery();
      if (isCurrencyAdded != 0) {
       resultSet = findCurrencyByCodeFromDB(currencyCode);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return resultSet;
  }


}
