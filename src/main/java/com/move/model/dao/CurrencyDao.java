package com.move.model.dao;

import com.move.resource.ConnectionDatabase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CurrencyDao {

  public ResultSet getCurrenciesFromDB() {
    String sqlQuery = "select * from currencies;";
    Connection connection;
    Statement statement;
    ResultSet resultSet;
    try {
      Class.forName("org.sqlite.JDBC");
      connection = ConnectionDatabase.getConnection();
      connection.setAutoCommit(false);
      statement = connection.createStatement();
      resultSet = statement.executeQuery(sqlQuery);
    } catch (ClassNotFoundException | SQLException e) {
      throw new RuntimeException(e);
    }
    return resultSet;
  }

}
