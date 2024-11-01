package com.move.service;

import com.move.model.CurrencyResponse;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CurrenciesService {

  public List<CurrencyResponse> getCurrencies(ResultSet resultSet) throws SQLException {
    List<CurrencyResponse> currencies = new ArrayList<>();
    while (resultSet.next()) {
      CurrencyResponse currencyResponse = CurrencyResponse.builder()
              .id(resultSet.getInt("id"))
              .code(resultSet.getString("code"))
              .fullName(resultSet.getString("full_name"))
              .sign(resultSet.getString("sign"))
              .build();

      currencies.add(currencyResponse);
    }
    return currencies;
  }


}
