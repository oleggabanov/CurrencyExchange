package com.move.dao.impl;

import com.move.dao.CurrencyDao;
import com.move.exception.EntityAlreadyExistsException;
import com.move.model.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDaoJDBC implements CurrencyDao {

  private Connection connection;

  public CurrencyDaoJDBC(Connection connection) {
    this.connection = connection;
  }

  public List<Currency> findAll() {
    try {
      String sqlQuery = "select * from currencies;";
      List<Currency> currencies = new ArrayList<>();
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(sqlQuery);
      while (resultSet.next()) {
        Currency currency = buildCurrency(resultSet);
        currencies.add(currency);
      }
      return currencies;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }


  @Override
  public Optional<Currency> findByCode(String currencyCode) {
    try {
      PreparedStatement preparedStatement = connection.prepareStatement("select * from currencies where code = (?);");
      preparedStatement.setString(1, currencyCode);
      ResultSet resultSet = preparedStatement.executeQuery();

      return resultSet.next() ? Optional.ofNullable(buildCurrency(resultSet)) : Optional.empty();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Currency save(Currency currency) {

    try {
      String currencyCode = currency.getCode();
      String fullName = currency.getFullName();
      String sign = currency.getSign();

      if (findByCode(currencyCode).isPresent()) {
        throw new EntityAlreadyExistsException("В базе данных уже есть валюта с кодом %s".formatted(currencyCode));
      }
      PreparedStatement preparedStatement = connection
              .prepareStatement("insert into currencies (code, full_name, sign) values (?,?,?);");
      preparedStatement.setString(1, currencyCode);
      preparedStatement.setString(2, fullName);
      preparedStatement.setString(3, sign);
      preparedStatement.executeUpdate();
      connection.commit();
      return findByCode(currencyCode)
              .orElseThrow(RuntimeException::new);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<Currency> findById(Integer currencyId) {
    try {
      PreparedStatement preparedStatement = connection.prepareStatement("select * from currencies where id = (?);");
      preparedStatement.setInt(1, currencyId);
      ResultSet resultSet = preparedStatement.executeQuery();
      return Optional.ofNullable(buildCurrency(resultSet));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void delete(Currency currency) {
    throw new UnsupportedOperationException("Not supported yet");
  }

  private static Currency buildCurrency(ResultSet resultSet) throws SQLException {
    return Currency.builder()
            .id(resultSet.getInt("id"))
            .code(resultSet.getString("code"))
            .fullName(resultSet.getString("full_name"))
            .sign(resultSet.getString("sign"))
            .build();
  }
}
