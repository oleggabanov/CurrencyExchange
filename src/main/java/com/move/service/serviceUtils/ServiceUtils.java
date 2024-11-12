package com.move.service.serviceUtils;

import com.move.model.Currency;
import com.move.model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServiceUtils {

  public static ExchangeRate getExchangeRate(int id, Currency baseCurrency, Currency targetCurrency, BigDecimal rate) throws SQLException {
    ExchangeRate exchangeRate = ExchangeRate.builder()
            .id(id)
            .baseCurrency(baseCurrency)
            .targetCurrency(targetCurrency)
            .rate(rate)
            .build();
    return exchangeRate;
  }

}
