package com.move.dao.impl;

import com.move.dao.AbstractDao;
import com.move.dao.CurrencyDao;
import com.move.exception.EntityAlreadyExistsException;
import com.move.model.Currency;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CurrencyDaoJDBC extends AbstractDao<Currency> implements CurrencyDao {

  private final Connection connection;

  public CurrencyDaoJDBC(Connection connection) {
    this.connection = connection;
  }

  public List<Currency> findAll() {
    String findAllCurrenciesQuery = "select * from currencies;";
    return executeQuery(
            findAllCurrenciesQuery,
            null,
            this::buildCurrency
    );
  }

  @Override
  public Optional<Currency> findByCode(String currencyCode) {
    String findCurrencyByCodeQuery = "select * from currencies where code = (?);";
    List<Currency> currencyList = executeQuery(
            findCurrencyByCodeQuery,
            preparedStatement ->
                    preparedStatement.setString(1, currencyCode),
            this::buildCurrency
    );

    return currencyList.isEmpty() ?
            Optional.empty() :
            Optional.of(currencyList.get(0));
  }

  @Override
  public Currency save(Currency currency) {
    String currencyCode = currency.getCode();
    String fullName = currency.getName();
    String sign = currency.getSign();

    if (findByCode(currencyCode).isPresent()) {
      throw new EntityAlreadyExistsException("В базе данных уже есть валюта с кодом %s".formatted(currencyCode));
    }

    String saveCurrencyQuery = "insert into currencies (code, full_name, sign) values (?,?,?);";
    executeUpdate(
            saveCurrencyQuery,
            preparedStatement -> {
              preparedStatement.setString(1, currencyCode);
              preparedStatement.setString(2, fullName);
              preparedStatement.setString(3, sign);
            }
    );

    return findByCode(currencyCode)
            .orElseThrow(RuntimeException::new);
  }

  @Override
  public Optional<Currency> findById(Integer currencyId) {
    String findByIdQuery = "select * from currencies where id = (?);";
    List<Currency> currencyList = executeQuery(
            findByIdQuery,
            preparedStatement -> preparedStatement.setInt(1, currencyId),
            this::buildCurrency
    );
    return currencyList.isEmpty() ?
            Optional.empty() :
            Optional.of(currencyList.get(0));
  }

  @Override
  public void delete(Currency currency) {
    throw new UnsupportedOperationException("Not supported yet");
  }

  private Currency buildCurrency(ResultSet resultSet) throws SQLException {
    return Currency.builder()
            .id(resultSet.getInt("id"))
            .code(resultSet.getString("code"))
            .name(resultSet.getString("full_name"))
            .sign(resultSet.getString("sign"))
            .build();
  }

}
