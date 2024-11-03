package com.move.service;

import com.move.dto.CurrencyDto;
import com.move.model.CurrencyResponse;
import com.move.model.dao.CurrencyDao;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CurrenciesService {

  private CurrencyDao currencyDao;

  public CurrenciesService() {
    this.currencyDao = new CurrencyDao();
  }

  @SneakyThrows
  public List<CurrencyResponse> getCurrencies() {
    ResultSet resultSet = currencyDao.findCurrenciesFromDB();
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

  @SneakyThrows
  public CurrencyResponse getCurrencyByCode(String currencyCode) {
    ResultSet resultSet = currencyDao.findCurrencyByCodeFromDB(currencyCode);

    return CurrencyResponse.builder()
            .id(resultSet.getInt("id"))
            .code(resultSet.getString("code"))
            .fullName(resultSet.getString("full_name"))
            .sign(resultSet.getString("sign"))
            .build();
  }

  @SneakyThrows
  public CurrencyResponse addCurrency(Map<String, String[]> requestParams) {
    ResultSet resultSet;
    CurrencyResponse currencyResponse = null;
    String code = requestParams.get("code")[0];
    String fullName = requestParams.get("full_name")[0];
    String sign = requestParams.get("sign")[0];
    if (code == null || fullName == null || sign == null) {
      throw new Exception();
    } else {
     CurrencyDto currencyDto = CurrencyDto.builder()
              .code(code)
              .fullName(fullName)
              .sign(sign)
              .build();

       resultSet = currencyDao.addCurrencyToDB(currencyDto);
       currencyResponse = CurrencyResponse.builder()
               .id(resultSet.getInt("id"))
               .code(resultSet.getString("code"))
               .fullName(resultSet.getString("full_name"))
               .sign(resultSet.getString("sign"))
               .build();
    }
    return currencyResponse;
  }


}
