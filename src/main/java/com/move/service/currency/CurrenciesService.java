package com.move.service.currency;

import com.move.dao.CurrenciesDao;
import com.move.dto.CurrencyDto;
import com.move.model.Currency;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CurrenciesService {

  private CurrenciesDao currenciesDao;

  public CurrenciesService() {
    this.currenciesDao = new CurrenciesDao();
  }

  @SneakyThrows
  public List<Currency> getCurrencies() {
    return currenciesDao.findAll();
  }

  @SneakyThrows
  public Currency getCurrencyByCode(String currencyCode) {
    Optional<Currency> findCurrencyByCode = currenciesDao.findCurrencyByCode(currencyCode);
    return findCurrencyByCode.orElseThrow(() -> new RuntimeException("No such currency"));
  }

  @SneakyThrows
  public Currency addCurrency(Map<String, String[]> requestParams) {
    Currency currency;
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

      Optional<Currency> findCurrencyByCode = currenciesDao.addCurrencyToDB(currencyDto);
      currency = findCurrencyByCode.orElseThrow(() -> new RuntimeException("No such currency"));
    }
    return currency;
  }
}
