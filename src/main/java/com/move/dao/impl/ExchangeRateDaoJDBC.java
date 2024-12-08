package com.move.dao.impl;

import com.move.dao.AbstractDao;
import com.move.dao.ExchangeRateDao;
import com.move.exception.EntityAlreadyExistsException;
import com.move.model.Currency;
import com.move.model.ExchangeRate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDaoJDBC extends AbstractDao<ExchangeRate> implements ExchangeRateDao {

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
    String findAllExchangeRatesQuery = "%s;".formatted(SQL_QUERY);
    return executeQuery(
            findAllExchangeRatesQuery,
            null,
            resultSet -> buildExchangeRate(resultSet)
    );
  }

  public Optional<ExchangeRate> findByCurrencyIds(int baseCurrencyId, int targetCurrencyId) {
    String findByCurrencyIdsQuery = """   
            %s
            where base_currency_id = (?) and target_currency_id = (?);
            """.formatted(SQL_QUERY);

    List<ExchangeRate> exchangeRates = executeQuery(
            findByCurrencyIdsQuery,
            preparedStatement -> {
              preparedStatement.setInt(1, baseCurrencyId);
              preparedStatement.setInt(2, targetCurrencyId);
            },
            this::buildExchangeRate
    );

    return exchangeRates.isEmpty() ?
            Optional.empty() :
            Optional.of(exchangeRates.get(0));
  }

  @Override
  public Optional<ExchangeRate> findByCurrencyCodes(String baseCurrencyCode, String targetCurrencyCode) {
    String findByCurrencyCodesQuery = """   
            %s
            where cb.code = (?) AND ct.code =(?);
            """.formatted(SQL_QUERY);

    List<ExchangeRate> exchangeRates = executeQuery(
            findByCurrencyCodesQuery,
            preparedStatement -> {
              preparedStatement.setString(1, baseCurrencyCode);
              preparedStatement.setString(2, targetCurrencyCode);
            },
            this::buildExchangeRate
    );

    return exchangeRates.isEmpty() ?
            Optional.empty() :
            Optional.of(exchangeRates.get(0));
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

    executeUpdate(
            sqlQuery,
            preparedStatement -> {
              preparedStatement.setBigDecimal(1, exchangeRate.getRate());
              preparedStatement.setInt(2, baseCurrencyId);
              preparedStatement.setInt(3, targetCurrencyId);
            }
    );

    return findByCurrencyIds(baseCurrencyId, targetCurrencyId)
            .orElseThrow(() ->
                    new EntityAlreadyExistsException("Данная валютная пара уже существует в базе данных"));
  }

  @Override
  public void delete(ExchangeRate exchangeRate) {
    String deleteSqlQuery = "DELETE FROM exchange_rates WHERE id = (?);";
    executeUpdate(deleteSqlQuery, preparedStatement ->
            preparedStatement.setInt(1, exchangeRate.getId()));
  }

  @Override
  public Optional<ExchangeRate> findById(Integer integer) {
    throw new UnsupportedOperationException("Not supported yet");
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
}
