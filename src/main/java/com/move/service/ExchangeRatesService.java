package com.move.service;

import com.move.dto.ExchangeRateDto;
import com.move.model.dao.CurrenciesDao;
import com.move.model.response.CurrencyResponse;
import com.move.model.response.ExchangeRateResponse;
import com.move.model.dao.ExchangeRatesDao;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.move.service.serviceUtils.ServiceUtils.getCurrencyFromResultSet;

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


  @SneakyThrows
  public ExchangeRateResponse getExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
    CurrencyResponse baseCurrency = getCurrencyFromResultSet(currenciesDao.findCurrencyByCodeFromDB(baseCurrencyCode));
    CurrencyResponse targetCurrency = getCurrencyFromResultSet(currenciesDao.findCurrencyByCodeFromDB(targetCurrencyCode));
    CurrencyResponse dollarCurrency = getCurrencyFromResultSet(currenciesDao.findCurrencyByCodeFromDB("USD"));
    ResultSet resultSet = exchangeRatesDao.findExchangeRateByCurrenciesId(baseCurrency.getId(), targetCurrency.getId());
    BigDecimal rate = resultSet.getBigDecimal("rate");
    if (rate != null) {
      int id = resultSet.getInt("id");
      return getExchangeRateResponse(id, baseCurrency, targetCurrency, rate);
    }

    resultSet = exchangeRatesDao.findExchangeRateByCurrenciesId(targetCurrency.getId(), baseCurrency.getId());
    rate = resultSet.getBigDecimal("rate");
    if (rate != null) {
      rate = BigDecimal.valueOf(1).divide(rate, 7, BigDecimal.ROUND_HALF_UP);
      ExchangeRateDto exchangeRateDto = ExchangeRateDto.builder()
              .baseCurrencyId(baseCurrency.getId())
              .targetCurrencyId(targetCurrency.getId())
              .rate(rate)
              .build();
      ResultSet savedExchangeRate = exchangeRatesDao.addExchangeRateToDB(exchangeRateDto);
      int id = savedExchangeRate.getInt("id");
      return getExchangeRateResponse(id, baseCurrency, targetCurrency, rate);
    }

    ResultSet baseResultSet = exchangeRatesDao.findExchangeRateByCurrenciesId(baseCurrency.getId(), dollarCurrency.getId());
    ResultSet targetResultSet = exchangeRatesDao.findExchangeRateByCurrenciesId(dollarCurrency.getId(), targetCurrency.getId());

    BigDecimal firstRate = baseResultSet.getBigDecimal("rate");
    BigDecimal secondRate = targetResultSet.getBigDecimal("rate");

    if (firstRate == null) {
      baseResultSet = exchangeRatesDao.findExchangeRateByCurrenciesId(dollarCurrency.getId(), baseCurrency.getId());
      firstRate = BigDecimal.valueOf(1).divide(baseResultSet.getBigDecimal("rate"), 7, BigDecimal.ROUND_HALF_UP);
    }
    if (secondRate == null) {
      targetResultSet = exchangeRatesDao.findExchangeRateByCurrenciesId(targetCurrency.getId(), dollarCurrency.getId());
      secondRate = BigDecimal.valueOf(1).divide(targetResultSet.getBigDecimal("rate"), 7, BigDecimal.ROUND_HALF_UP);
    }

    rate = firstRate.multiply(secondRate);
    ExchangeRateDto exchangeRateDto = ExchangeRateDto.builder()
            .baseCurrencyId(baseCurrency.getId())
            .targetCurrencyId(targetCurrency.getId())
            .rate(rate)
            .build();
    ResultSet savedExchangeRate = exchangeRatesDao.addExchangeRateToDB(exchangeRateDto);
    return getExchangeRateResponse(savedExchangeRate.getInt("id"), baseCurrency, targetCurrency, rate);
  }

  private ExchangeRateResponse getExchangeRateResponse(int id, CurrencyResponse baseCurrency, CurrencyResponse targetCurrency, BigDecimal rate) throws SQLException {
    ExchangeRateResponse exchangeRateResponse = ExchangeRateResponse.builder()
            .id(id)
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

  @SneakyThrows
  public ExchangeRateResponse updateExchangeRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
    CurrencyResponse baseCurrency = getCurrencyFromResultSet(currenciesDao.findCurrencyByCodeFromDB(baseCurrencyCode));
    CurrencyResponse targetCurrency = getCurrencyFromResultSet(currenciesDao.findCurrencyByCodeFromDB(targetCurrencyCode));

    ExchangeRateDto exchangeRateDto = ExchangeRateDto.builder()
            .baseCurrencyId(baseCurrency.getId())
            .targetCurrencyId(targetCurrency.getId())
            .rate(rate)
            .build();

    ResultSet resultSet = exchangeRatesDao.updateExchangeRateInDB(exchangeRateDto);

    ExchangeRateResponse exchangeRateResponse = ExchangeRateResponse.builder()
            .id(resultSet.getInt("id"))
            .baseCurrency(baseCurrency)
            .targetCurrency(targetCurrency)
            .rate(resultSet.getBigDecimal("rate"))
            .build();

    return exchangeRateResponse;
  }

  @SneakyThrows
  public int deleteExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
    CurrencyResponse baseCurrency = getCurrencyFromResultSet(currenciesDao.findCurrencyByCodeFromDB(baseCurrencyCode));
    CurrencyResponse targetCurrency = getCurrencyFromResultSet(currenciesDao.findCurrencyByCodeFromDB(targetCurrencyCode));
    boolean isExchangeRateDeleted = exchangeRatesDao.deleteExchangeRateByCurrencyCodesFromDB(baseCurrency.getId(), targetCurrency.getId());
    return isExchangeRateDeleted ? HttpServletResponse.SC_OK : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
  }
}
