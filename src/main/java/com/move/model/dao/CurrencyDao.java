package com.move.model.dao;

import com.move.model.CurrencyResponse;
import com.move.resource.ConnectionDatabase;
import lombok.AllArgsConstructor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CurrencyDao {

  public List<CurrencyResponse> getCurrencies() {
    List<CurrencyResponse> currencies = new ArrayList<>();
    String sqlQuery = "select * from currencies;";
    Connection connection;
    Statement statement;
    try {
      Class.forName("org.sqlite.JDBC");
      connection = ConnectionDatabase.getConnection();
      connection.setAutoCommit(false);
      statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(sqlQuery);
      while (resultSet.next()) {
        CurrencyResponse currencyResponse = CurrencyResponse.builder()
                .id(resultSet.getInt("id"))
                .code(resultSet.getString("code"))
                .fullName(resultSet.getString("full_name"))
                .sign(resultSet.getString("sign"))
                .build();

        currencies.add(currencyResponse);
      }

    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }


    return currencies;
  }


}
