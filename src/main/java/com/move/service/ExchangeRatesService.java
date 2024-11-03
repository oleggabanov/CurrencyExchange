package com.move.service;

import com.move.model.ExchangeRatesResponse;
import com.move.model.dao.ExchangeRatesDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRatesService {

  private ExchangeRatesDao exchangeRatesDao;

  public ExchangeRatesService() {
    this.exchangeRatesDao = new ExchangeRatesDao();
  }

  public List<ExchangeRatesResponse> getAllExchangeRates() throws SQLException {
    ResultSet resultSet = exchangeRatesDao.findExchangeRatesFromDB();
    List<ExchangeRatesResponse> exchangeRates = new ArrayList<>();
    while (resultSet.next()) {
      ExchangeRatesResponse exchangeRatesResponse = ExchangeRatesResponse.builder()
              .id(resultSet.getInt("id"))
              .baseCurrencyId(resultSet.getString("base_currency_id"))
              .targetCurrencyId(resultSet.getString("target_currency_id"))
              .rate(resultSet.getBigDecimal("rate"))
              .build();

      exchangeRates.add(exchangeRatesResponse);
    }
    return exchangeRates;
  }
}
