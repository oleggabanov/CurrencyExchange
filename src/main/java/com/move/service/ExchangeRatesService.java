package com.move.service;

import com.move.dto.ExchangeRateDto;
import com.move.model.dao.CurrenciesDao;
import com.move.model.response.CurrencyResponse;
import com.move.model.response.ExchangeRateResponse;
import com.move.model.dao.ExchangeRatesDao;
import lombok.SneakyThrows;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRatesService {

  private ExchangeRatesDao exchangeRatesDao;
  private CurrenciesDao currenciesDao;

  public ExchangeRatesService() {
    this.exchangeRatesDao = new ExchangeRatesDao();
    this.currenciesDao = new CurrenciesDao();
  }

  public List<ExchangeRateResponse> getAllExchangeRates() throws SQLException {
    ResultSet resultSet = exchangeRatesDao.findExchangeRatesFromDB();
    List<ExchangeRateResponse> exchangeRates = new ArrayList<>();
    while (resultSet.next()) {
      int baseCurrencyId = resultSet.getInt("base_currency_id");
      int targetCurrencyId = resultSet.getInt("target_currency_id");
      CurrencyResponse baseCurrency = getCurrencyFromResultSet(currenciesDao.findCurrencyByIdFromDB(baseCurrencyId));
      CurrencyResponse targetCurrency = getCurrencyFromResultSet(currenciesDao.findCurrencyByIdFromDB(targetCurrencyId));
      ExchangeRateResponse exchangeRateResponse = ExchangeRateResponse.builder()
              .id(resultSet.getInt("id"))
              .baseCurrency(baseCurrency)
              .targetCurrency(targetCurrency)
              .rate(resultSet.getBigDecimal("rate"))
              .build();

      exchangeRates.add(exchangeRateResponse);
    }
    return exchangeRates;
  }

  public CurrencyResponse getCurrencyFromResultSet(ResultSet resultSet) throws SQLException {
    return CurrencyResponse.builder()
            .id(resultSet.getInt("id"))
            .code(resultSet.getString("code"))
            .fullName(resultSet.getString("full_name"))
            .sign(resultSet.getString("sign"))
            .build();
  }

  @SneakyThrows
  public ExchangeRateResponse getExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
    ExchangeRateResponse exchangeRateResponse;
    CurrencyResponse baseCurrency = getCurrencyFromResultSet(currenciesDao.findCurrencyByCodeFromDB(baseCurrencyCode));
    CurrencyResponse targetCurrency = getCurrencyFromResultSet(currenciesDao.findCurrencyByCodeFromDB(targetCurrencyCode));
    ResultSet resultSet = exchangeRatesDao.findExchangeRateByCurrencyId(baseCurrency.getId(), targetCurrency.getId());
    BigDecimal rate = resultSet.getBigDecimal("rate");
    if (resultSet.getBigDecimal("rate") == null) {
      ResultSet resultSet2 = exchangeRatesDao.findExchangeRateByCurrencyId(targetCurrency.getId(), baseCurrency.getId());
      if (resultSet2.getBigDecimal("rate") != null) {
        rate = new BigDecimal(1.0).divide(resultSet2.getBigDecimal("rate"), 10, BigDecimal.ROUND_HALF_UP);
      }
    }
    exchangeRateResponse = ExchangeRateResponse.builder()
            .id(resultSet.getInt("id"))
            .baseCurrency(baseCurrency)
            .targetCurrency(targetCurrency)
            .rate(rate)
            .build();
    return exchangeRateResponse;
  }

  @SneakyThrows
  public ExchangeRateResponse addExchangeRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
    CurrencyResponse baseCurrency = getCurrencyFromResultSet(currenciesDao.findCurrencyByCodeFromDB(baseCurrencyCode));
    CurrencyResponse targetCurrency = getCurrencyFromResultSet(currenciesDao.findCurrencyByCodeFromDB(targetCurrencyCode));
    ExchangeRateDto exchangeRateDto = ExchangeRateDto.builder()
            .baseCurrencyId(baseCurrency.getId())
            .targetCurrencyId(targetCurrency.getId())
            .rate(rate)
            .build();
    ResultSet resultSet = exchangeRatesDao.addExchangeRateToDB(exchangeRateDto);

    ExchangeRateResponse exchangeRateResponse = ExchangeRateResponse.builder()
            .id(resultSet.getInt("id"))
            .baseCurrency(baseCurrency)
            .targetCurrency(targetCurrency)
            .rate(resultSet.getBigDecimal("rate"))
            .build();
    return exchangeRateResponse;
  }
}
