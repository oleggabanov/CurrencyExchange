package com.move.service.exchange;

import com.move.dao.CurrenciesDao;
import com.move.dao.ExchangeRatesDao;
import com.move.dto.ExchangeRateDto;
import com.move.model.CurrencyResponse;
import com.move.model.ExchangeRate;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.move.service.serviceUtils.ServiceUtils.getCurrencyFromResultSet;
import static com.move.service.serviceUtils.ServiceUtils.getExchangeRate;

public class ExchangeRatesService {

  private ExchangeRatesDao exchangeRatesDao;
  private CurrenciesDao currenciesDao;

  public ExchangeRatesService() {
    this.exchangeRatesDao = new ExchangeRatesDao();
    this.currenciesDao = new CurrenciesDao();
  }

  public List<ExchangeRate> getAllExchangeRates() throws SQLException {
    ResultSet resultSet = exchangeRatesDao.findExchangeRatesFromDB();
    List<ExchangeRate> exchangeRates = new ArrayList<>();
    while (resultSet.next()) {
      int baseCurrencyId = resultSet.getInt("base_currency_id");
      int targetCurrencyId = resultSet.getInt("target_currency_id");
      CurrencyResponse baseCurrency = getCurrencyFromResultSet(currenciesDao.findCurrencyByIdFromDB(baseCurrencyId));
      CurrencyResponse targetCurrency = getCurrencyFromResultSet(currenciesDao.findCurrencyByIdFromDB(targetCurrencyId));
      ExchangeRate exchangeRate = ExchangeRate.builder()
              .id(resultSet.getInt("id"))
              .baseCurrency(baseCurrency)
              .targetCurrency(targetCurrency)
              .rate(resultSet.getBigDecimal("rate"))
              .build();

      exchangeRates.add(exchangeRate);
    }

    return exchangeRates;
  }


  @SneakyThrows
  public ExchangeRate getExchangeRateByCurrencyCodes(String baseCurrencyCode, String targetCurrencyCode) {
    CurrencyResponse baseCurrency = getCurrencyFromResultSet(currenciesDao.findCurrencyByCodeFromDB(baseCurrencyCode));
    CurrencyResponse targetCurrency = getCurrencyFromResultSet(currenciesDao.findCurrencyByCodeFromDB(targetCurrencyCode));
    ExchangeRate exchangeRate;

    exchangeRate = getStraightAndReverseExchangeRate(baseCurrency, targetCurrency);
    if (exchangeRate == null) {
      exchangeRate = getCrossExchangeRate(baseCurrency, targetCurrency);
    }

    return exchangeRate;
  }

  private ExchangeRate getStraightAndReverseExchangeRate(CurrencyResponse baseCurrency, CurrencyResponse targetCurrency) throws SQLException {
    ResultSet resultSet = exchangeRatesDao.findExchangeRateByCurrenciesId(baseCurrency.getId(), targetCurrency.getId());
    BigDecimal rate = resultSet.getBigDecimal("rate");

    if (rate != null) {
      int id = resultSet.getInt("id");
      return getExchangeRate(id, baseCurrency, targetCurrency, rate);
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
      return getExchangeRate(id, baseCurrency, targetCurrency, rate);
    }
    return null;
  }

  private ExchangeRate getCrossExchangeRate(CurrencyResponse baseCurrency, CurrencyResponse targetCurrency) throws SQLException {

    ExchangeRate exchangeRate1 = null;
    ExchangeRate exchangeRate2 = null;
    BigDecimal rate;
    CurrencyResponse currency1;
    CurrencyResponse currency2;
    for (ExchangeRate eRate : getAllExchangeRates()) {
      currency1 = eRate.getBaseCurrency();
      currency2 = eRate.getTargetCurrency();

      if (baseCurrency.getCode().equals(currency1.getCode())) {
        exchangeRate1 = getStraightAndReverseExchangeRate(baseCurrency, currency2);
        exchangeRate2 = getStraightAndReverseExchangeRate(currency2, targetCurrency);
        break;
      }

      if (baseCurrency.getCode().equals(currency2.getCode())) {
        exchangeRate1 = getStraightAndReverseExchangeRate(baseCurrency, currency1);
        exchangeRate2 = getStraightAndReverseExchangeRate(currency1, targetCurrency);
        break;
      }

    }
    rate = exchangeRate1.getRate().multiply(exchangeRate2.getRate());
    exchangeRate1 = addExchangeRate(baseCurrency.getCode(), targetCurrency.getCode(), rate);

    return exchangeRate1;
  }


  @SneakyThrows
  public ExchangeRate addExchangeRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
    CurrencyResponse baseCurrency = getCurrencyFromResultSet(currenciesDao.findCurrencyByCodeFromDB(baseCurrencyCode));
    CurrencyResponse targetCurrency = getCurrencyFromResultSet(currenciesDao.findCurrencyByCodeFromDB(targetCurrencyCode));
    ExchangeRateDto exchangeRateDto = ExchangeRateDto.builder()
            .baseCurrencyId(baseCurrency.getId())
            .targetCurrencyId(targetCurrency.getId())
            .rate(rate)
            .build();
    ResultSet resultSet = exchangeRatesDao.addExchangeRateToDB(exchangeRateDto);

    ExchangeRate exchangeRate = ExchangeRate.builder()
            .id(resultSet.getInt("id"))
            .baseCurrency(baseCurrency)
            .targetCurrency(targetCurrency)
            .rate(resultSet.getBigDecimal("rate"))
            .build();
    return exchangeRate;
  }

  @SneakyThrows
  public ExchangeRate updateExchangeRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
    CurrencyResponse baseCurrency = getCurrencyFromResultSet(currenciesDao.findCurrencyByCodeFromDB(baseCurrencyCode));
    CurrencyResponse targetCurrency = getCurrencyFromResultSet(currenciesDao.findCurrencyByCodeFromDB(targetCurrencyCode));

    ExchangeRateDto exchangeRateDto = ExchangeRateDto.builder()
            .baseCurrencyId(baseCurrency.getId())
            .targetCurrencyId(targetCurrency.getId())
            .rate(rate)
            .build();

    ResultSet resultSet = exchangeRatesDao.updateExchangeRateInDB(exchangeRateDto);

    ExchangeRate exchangeRate = ExchangeRate.builder()
            .id(resultSet.getInt("id"))
            .baseCurrency(baseCurrency)
            .targetCurrency(targetCurrency)
            .rate(resultSet.getBigDecimal("rate"))
            .build();

    return exchangeRate;
  }

  @SneakyThrows
  public int deleteExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
    CurrencyResponse baseCurrency = getCurrencyFromResultSet(currenciesDao.findCurrencyByCodeFromDB(baseCurrencyCode));
    CurrencyResponse targetCurrency = getCurrencyFromResultSet(currenciesDao.findCurrencyByCodeFromDB(targetCurrencyCode));
    ResultSet resultSet = exchangeRatesDao.findExchangeRateByCurrenciesId(baseCurrency.getId(), targetCurrency.getId());
    boolean isExchangeRateDeleted = exchangeRatesDao.deleteExchangeRateByCurrencyCodesFromDB(resultSet.getInt("id"));
    return isExchangeRateDeleted ? HttpServletResponse.SC_OK : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
  }
}
