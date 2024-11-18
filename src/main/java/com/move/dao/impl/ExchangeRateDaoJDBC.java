package com.move.dao.impl;

import com.move.dao.ExchangeRateDao;
import com.move.exception.EntityAlreadyExistsException;
import com.move.model.Currency;
import com.move.model.ExchangeRate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDaoJDBC implements ExchangeRateDao {

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

  private final Connection connection;


  public ExchangeRateDaoJDBC(Connection connection) {
    this.connection = connection;
  }

  @Override
  public List<ExchangeRate> findAll() {
    String findAllExchangeRatesQuery = """
            %s;
            """;
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(findAllExchangeRatesQuery.formatted(SQL_QUERY));
      ResultSet resultSet = preparedStatement.executeQuery();
      List<ExchangeRate> exchangeRates = new ArrayList<>();
      while (resultSet.next()) {
        ExchangeRate exchangeRate = buildExchangeRate(resultSet);
        exchangeRates.add(exchangeRate);
      }
      return exchangeRates;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private ExchangeRate buildExchangeRate(ResultSet resultSet) throws SQLException {
    return ExchangeRate.builder()
            .id(resultSet.getInt("exchange_rate_id"))
            .baseCurrency(buildCurrency(resultSet, "base"))
            .targetCurrency(buildCurrency(resultSet, "target"))
            .rate(resultSet.getBigDecimal("rate"))
            .build();
  }

  private static Currency buildCurrency(ResultSet resultSet, String prefix) throws SQLException {
    return Currency.builder()
            .id(resultSet.getInt("%s_currency_id".formatted(prefix)))
            .code(resultSet.getString("%s_currency_code".formatted(prefix)))
            .name(resultSet.getString("%s_currency_name".formatted(prefix)))
            .sign(resultSet.getString("%s_currency_sign".formatted(prefix)))
            .build();
  }

  public Optional<ExchangeRate> findByCurrencyIds(int baseCurrencyId, int targetCurrencyId) {
    String sqlQuery = """   
            %s
            where base_currency_id = (?) and target_currency_id = (?);
            """.formatted(SQL_QUERY);
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
      preparedStatement.setInt(1, baseCurrencyId);
      preparedStatement.setInt(2, targetCurrencyId);
      ResultSet resultSet = preparedStatement.executeQuery();
      ExchangeRate exchangeRate = buildExchangeRate(resultSet);

      return exchangeRate.getRate() != null ?
              Optional.ofNullable(exchangeRate) :
              Optional.empty();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<ExchangeRate> findByCurrencyCodes(String baseCurrencyCode, String targetCurrencyCode) {
    String sqlQuery = """   
            %s
            where cb.code = (?) AND ct.code =(?);
            """.formatted(SQL_QUERY);
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
      preparedStatement.setString(1, baseCurrencyCode);
      preparedStatement.setString(2, targetCurrencyCode);
      ResultSet resultSet = preparedStatement.executeQuery();
      ExchangeRate exchangeRate = buildExchangeRate(resultSet);

      return exchangeRate.getRate() != null ?
              Optional.ofNullable(exchangeRate) :
              Optional.empty();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ExchangeRate save(ExchangeRate exchangeRate) {
    Optional<ExchangeRate> findByIds = findByCurrencyIds(
            exchangeRate.getBaseCurrency().getId(),
            exchangeRate.getTargetCurrency().getId()
    );
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

      return findByCurrencyIds(baseCurrencyId, targetCurrencyId)
              .orElseThrow(() ->
                      new EntityAlreadyExistsException("Данная валютная пара уже существует в базе данных"));
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
