package com.move.service;

import com.move.model.ExchangeRatesResponse;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRatesService {


  public List<ExchangeRatesResponse> getAllExchangeRates(ResultSet resultSet) throws SQLException {
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
