package com.move.dao;

import com.move.exceptions.EntityAlreadyExistsException;
import com.move.exceptions.EntityNotFoundException;
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
        Currency currency = Currency.builder()
                .id(resultSet.getInt("id"))
                .code(resultSet.getString("code"))
                .fullName(resultSet.getString("full_name"))
                .sign(resultSet.getString("sign"))
                .build();

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
      if (resultSet.getString("code") == null) {
        throw new EntityNotFoundException("Currency not found");
      }
      return Optional.ofNullable(Currency.builder()
              .id(resultSet.getInt("id"))
              .code(resultSet.getString("code"))
              .fullName(resultSet.getString("full_name"))
              .sign(resultSet.getString("sign"))
              .build());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Currency save(Currency newCurrency) {
    try {
      String currencyCode = newCurrency.getCode();
      String fullName = newCurrency.getFullName();
      String sign = newCurrency.getSign();
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
      if (e.getErrorCode() == 19) { //check existence of currency
        throw new EntityAlreadyExistsException("Такая валюта уже есть в бд");
      }
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<Currency> findById(Integer currencyId) {
    try {
      PreparedStatement preparedStatement = connection.prepareStatement("select * from currencies where id = (?);");
      preparedStatement.setInt(1, currencyId);
      ResultSet resultSet = preparedStatement.executeQuery();
      return Optional.ofNullable(Currency.builder()
              .id(resultSet.getInt("id"))
              .code(resultSet.getString("code"))
              .fullName(resultSet.getString("full_name"))
              .sign(resultSet.getString("sign"))
              .build());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void delete(Currency currency) {
    throw new UnsupportedOperationException("Not supported yet");
  }
}
