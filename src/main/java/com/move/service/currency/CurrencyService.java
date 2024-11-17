package com.move.service.currency;

import com.move.context.AppContext;
import com.move.dao.CurrencyDao;
import com.move.dto.CurrencyDto;
import com.move.exception.EntityNotFoundException;
import com.move.mapper.CustomMapper;
import com.move.model.Currency;

import java.util.List;

public class CurrencyService {

  private final CurrencyDao currencyDao;
  private final CustomMapper<Currency, CurrencyDto> customMapper;

  public CurrencyService() {
    this.currencyDao = AppContext.getInstance().getCurrencyDao();
    this.customMapper = AppContext.getInstance().getCustomMapper();
  }

  public List<Currency> getCurrencies() {
    return currencyDao.findAll();
  }

  public Currency getCurrencyByCode(String currencyCode) {
    return currencyDao.findByCode(currencyCode)
            .orElseThrow(() -> new EntityNotFoundException("Валюта не найдена, проверьте корректность вводимых данных и повторите попытку"));
  }

  public Currency addCurrency(CurrencyDto currencyDto) {
    Currency currency = customMapper.map(currencyDto);
    return currencyDao.save(currency);
  }

}
