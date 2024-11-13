package com.move.dao.impl;

import com.move.dao.ExchangeRateDao;
import com.move.model.Currency;
import com.move.model.ExchangeRate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDaoJDBC implements ExchangeRateDao {

  private Connection connection;
  private static final String SQL_QUERY = """
           SELECT er.id        AS exchange_rate_id,
                   cb.id        AS base_currency_id,
                   cb.code      AS base_currency_code,
                   cb.full_name AS base_currency_name,
                   cb.sign      AS base_currency_sign,
                   ct.id        AS target_currency_id,
                   ct.code      AS target_currency_code,
                   ct.full_name AS target_currency_name,
                   ct.sign      AS target_currency_sign,
                   er.rate      as rate
            FROM exchange_rates er
                     JOIN
                 currencies cb ON er.base_currency_id = cb.id
                     JOIN
                 currencies ct ON er.target_currency_id = ct.id
          """;

  public ExchangeRateDaoJDBC(Connection connection) {
    this.connection = connection;
  }

  @Override
  public List<ExchangeRate> findAll() {
    try {
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(SQL_QUERY + ";");
      List<ExchangeRate> exchangeRates = new ArrayList<>();
      while (resultSet.next()) {
        Currency baseCurrency = Currency.builder()
                .id(resultSet.getInt("base_currency_id"))
                .code(resultSet.getString("base_currency_code"))
                .fullName(resultSet.getString("base_currency_name"))
                .sign(resultSet.getString("base_currency_sign"))
                .build();

        Currency targetCurrency = Currency.builder()
                .id(resultSet.getInt("target_currency_id"))
                .code(resultSet.getString("target_currency_code"))
                .fullName(resultSet.getString("target_currency_name"))
                .sign(resultSet.getString("target_currency_sign"))
                .build();

        ExchangeRate exchangeRate = ExchangeRate.builder()
                .id(resultSet.getInt("exchange_rate_id"))
                .baseCurrency(baseCurrency)
                .targetCurrency(targetCurrency)
                .rate(resultSet.getBigDecimal("rate"))
                .build();

        exchangeRates.add(exchangeRate);
      }
      return exchangeRates;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

  }

  public Optional<ExchangeRate> findByCurrencyIds(int baseCurrencyId, int targetCurrencyId) {
    String sqlQuery = """   
            %s
            where base_currency_id = (?) and target_currency_id = (?);
            """.formatted(SQL_QUERY);
    ResultSet resultSet;

    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
      preparedStatement.setInt(1, baseCurrencyId);
      preparedStatement.setInt(2, targetCurrencyId);
      resultSet = preparedStatement.executeQuery();
      Currency baseCurrency = Currency.builder()
              .id(resultSet.getInt("base_currency_id"))
              .code(resultSet.getString("base_currency_code"))
              .fullName(resultSet.getString("base_currency_name"))
              .sign(resultSet.getString("base_currency_sign"))
              .build();

      Currency targetCurrency = Currency.builder()
              .id(resultSet.getInt("target_currency_id"))
              .code(resultSet.getString("target_currency_code"))
              .fullName(resultSet.getString("target_currency_name"))
              .sign(resultSet.getString("target_currency_sign"))
              .build();

      ExchangeRate exchangeRate = ExchangeRate.builder()
              .id(resultSet.getInt("exchange_rate_id"))
              .baseCurrency(baseCurrency)
              .targetCurrency(targetCurrency)
              .rate(resultSet.getBigDecimal("rate"))
              .build();

      return exchangeRate.getRate() != null ? Optional.ofNullable(exchangeRate) : Optional.empty();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ExchangeRate save(ExchangeRate exchangeRate) {
    Optional<ExchangeRate> findByIds = findByCurrencyIds(exchangeRate.getBaseCurrency().getId(), exchangeRate.getTargetCurrency().getId());
    String sqlQuery = findByIds.isPresent()
            ? "update exchange_rates set rate = (?) where base_currency_id = (?) and target_currency_id = (?);"
            : "insert into exchange_rates (rate, base_currency_id, target_currency_id) values (?, ?, ?);";
    int baseCurrencyId = exchangeRate.getBaseCurrency().getId();
    int targetCurrencyId = exchangeRate.getTargetCurrency().getId();
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
      preparedStatement.setBigDecimal(1, exchangeRate.getRate());
      preparedStatement.setInt(2, baseCurrencyId);
      preparedStatement.setInt(3, targetCurrencyId);
      preparedStatement.executeUpdate();
      connection.commit();
      return findByCurrencyIds(baseCurrencyId, targetCurrencyId).orElseThrow(RuntimeException::new);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }


  @Override
  public void delete(ExchangeRate exchangeRate) {
    String deleteSqlQuery = "DELETE FROM exchange_rates WHERE id = (?);";
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(deleteSqlQuery);
      preparedStatement.setInt(1, exchangeRate.getId());
      preparedStatement.executeUpdate();
      connection.commit();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<ExchangeRate> findById(Integer integer) {
    throw new UnsupportedOperationException("Not supported yet");
  }
}
