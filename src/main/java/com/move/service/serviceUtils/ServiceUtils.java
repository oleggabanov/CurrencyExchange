package com.move.service.serviceUtils;

import com.move.model.CurrencyResponse;
import com.move.model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServiceUtils {

  public static CurrencyResponse getCurrencyFromResultSet(ResultSet resultSet) throws SQLException {
    return CurrencyResponse.builder()
            .id(resultSet.getInt("id"))
            .code(resultSet.getString("code"))
            .fullName(resultSet.getString("full_name"))
            .sign(resultSet.getString("sign"))
            .build();
  }

  public static ExchangeRate getExchangeRate(int id, CurrencyResponse baseCurrency, CurrencyResponse targetCurrency, BigDecimal rate) throws SQLException {
    ExchangeRate exchangeRate = ExchangeRate.builder()
            .id(id)
            .baseCurrency(baseCurrency)
            .targetCurrency(targetCurrency)
            .rate(rate)
            .build();
    return exchangeRate;
  }

}
