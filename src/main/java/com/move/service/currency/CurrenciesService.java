package com.move.service.currency;

import com.move.context.AppContext;
import com.move.dao.CurrencyDao;
import com.move.dto.CurrencyDto;
import com.move.model.Currency;

import java.util.List;

public class CurrenciesService {

  private CurrencyDao currencyDao;

  public CurrenciesService() {
    this.currencyDao = AppContext.getInstance().getCurrencyDao();
  }

  public List<Currency> getCurrencies() {
    return currencyDao.findAll();
  }

  public Currency getCurrencyByCode(String currencyCode) {
    return currencyDao.findByCode(currencyCode)
            .orElseThrow(RuntimeException::new);
  }

  public Currency addCurrency(CurrencyDto currencyParams) {
    String code = currencyParams.code();
    String fullName = currencyParams.fullName();
    String sign = currencyParams.sign();

    Currency currency = Currency.builder()
            .code(code)
            .fullName(fullName)
            .sign(sign)
            .build();

    return currencyDao.save(currency);
  }
}
